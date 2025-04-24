package com.vulinh.configuration;

import com.vulinh.data.constant.UserRole;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.SecurityUrlUtils;
import com.vulinh.utils.security.AccessTokenValidator;
import com.vulinh.utils.security.Auth0Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

  private static final UserRole ROLE_ADMIN = UserRole.ADMIN;

  private final ApplicationProperties applicationProperties;

  private final HandlerExceptionResolver handlerExceptionResolver;
  private final AccessTokenValidator accessTokenValidator;
  private final CustomAuthenticationManager customAuthenticationManager;

  @Bean
  @SneakyThrows
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
    return httpSecurity
        .headers(
            shc ->
                shc.xssProtection(
                        xssConfig -> xssConfig.headerValue(HeaderValue.ENABLED_MODE_BLOCK))
                    .contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'")))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(
            sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(this::configureAuthorizeHttpRequestCustomizer)
        .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(createCorsFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptionHandlingCustomizer ->
                exceptionHandlingCustomizer
                    .accessDeniedHandler(this::handleSecurityException)
                    .authenticationEntryPoint(this::handleSecurityException))
        .build();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(UserRole.toHierarchyPhrase());
  }

  private CorsFilter createCorsFilter() {
    var config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    var source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  private void configureAuthorizeHttpRequestCustomizer(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorizeHttpRequestsCustomizer) {
    var securityProperties = applicationProperties.security();

    for (var verbUrl : securityProperties.allowedVerbUrls()) {
      authorizeHttpRequestsCustomizer.requestMatchers(verbUrl.method(), verbUrl.url()).permitAll();
    }

    for (var privilegeVerbUrl : SecurityUrlUtils.getVerbUrlsWithPrivilege(ROLE_ADMIN)) {
      authorizeHttpRequestsCustomizer
          .requestMatchers(privilegeVerbUrl.method(), privilegeVerbUrl.url())
          .hasAuthority(ROLE_ADMIN.name());
    }

    authorizeHttpRequestsCustomizer
        .requestMatchers(securityProperties.noAuthenticatedUrls().toArray(String[]::new))
        .permitAll()
        .requestMatchers(SecurityUrlUtils.getUrlsWithPrivilege(ROLE_ADMIN))
        .hasAuthority(ROLE_ADMIN.name())
        .anyRequest()
        .authenticated();
  }

  private void handleSecurityException(
      HttpServletRequest request, HttpServletResponse response, Throwable authException) {
    handlerExceptionResolver.resolveException(
        request, response, null, ExceptionFactory.INSTANCE.invalidAuthorization(authException));
  }

  // Temporary solution to avoid filter being called twice for every request
  private class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) {
      try {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null) {
          var jwtPayload =
              accessTokenValidator.validateAccessToken(Auth0Utils.parseBearerToken(header));

          var authentication =
              customAuthenticationManager.authenticate(
                  new PreAuthenticatedAuthenticationToken(jwtPayload, null));

          var customAuthentication =
              authentication.addDetails(
                  new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
                      request, authentication.getAuthorities()));

          SecurityContextHolder.getContext().setAuthentication(customAuthentication);
        }

        filterChain.doFilter(request, response);
      } catch (Exception exception) {
        handlerExceptionResolver.resolveException(request, response, null, exception);
      }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
      var requestURI = request.getRequestURI();

      var securityProperties = applicationProperties.security();

      return securityProperties.noAuthenticatedUrls().stream()
              .anyMatch(
                  antUrl -> {
                    var result = ANT_PATH_MATCHER.match(antUrl, requestURI);

                    if (result) {
                      log.debug(
                          "Request URI [{}] matched against ant pattern [{}]", requestURI, antUrl);
                    }

                    return result;
                  })
          || securityProperties.allowedVerbUrls().stream()
              .anyMatch(
                  verbAntUrl -> {
                    var requestMethod = request.getMethod();

                    var antUrl = verbAntUrl.url();
                    var antMethod = verbAntUrl.method();

                    var result =
                        antMethod.name().equals(requestMethod)
                            && ANT_PATH_MATCHER.match(antUrl, requestURI);

                    if (result) {
                      log.debug(
                          "Request URI [{} {}] matched against ant pattern [{} {}]",
                          requestMethod,
                          requestURI,
                          antMethod,
                          antUrl);
                    }

                    return result;
                  });
    }
  }
}
