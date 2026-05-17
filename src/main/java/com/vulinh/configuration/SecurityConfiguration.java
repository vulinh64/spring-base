package com.vulinh.configuration;

import module java.base;

import com.vulinh.configuration.CaffeineCacheConfiguration.CacheProperties;
import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.configuration.data.ApplicationProperties.SecurityProperties;
import com.vulinh.data.constant.UserRole;
import com.vulinh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

  // One of the best and the most elegant ways to handle exceptions in Spring Security filters
  private final HandlerExceptionResolver handlerExceptionResolver;

  static final String ROLE_ADMIN_NAME = UserRole.ADMIN.name();

  static final String EXPECTED_TYP = "access";

  static final String ACCESS_TOKEN_COOKIE = "access_token";

  // Skips OAuth2 token decoding entirely for no-auth URLs so a stale access_token cookie can't 403
  // them.
  @Bean
  @Order(1)
  @SneakyThrows
  public SecurityFilterChain publicSecurityFilterChain(
      HttpSecurity httpSecurity, ApplicationProperties applicationProperties) {
    return applyCommonSecurity(httpSecurity)
        .securityMatcher(
            applicationProperties.security().noAuthenticatedUrls().toArray(String[]::new))
        .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
        .build();
  }

  @Bean
  @Order(2)
  @SneakyThrows
  public SecurityFilterChain securityFilterChain(
      HttpSecurity httpSecurity, ApplicationProperties applicationProperties) {
    var security = applicationProperties.security();

    return applyCommonSecurity(httpSecurity)
        .authorizeHttpRequests(
            authorizeHttpRequestsCustomizer ->
                configureAuthorizeHttpRequestCustomizer(authorizeHttpRequestsCustomizer, security))
        .oauth2ResourceServer(
            oAuth2ResourceServerProperties ->
                oAuth2ResourceServerProperties
                    // Return something to client rather than a blank 403 page
                    .accessDeniedHandler(this::delegateToHandlerExceptionResolver)
                    // Return something to client rather than a blank 401 page
                    .authenticationEntryPoint(this::delegateToHandlerExceptionResolver)
                    .jwt(
                        jwtConfigurer ->
                            jwtConfigurer.jwtAuthenticationConverter(
                                JwtUtils::parseAuthoritiesByCustomClaims)))
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder(
      ApplicationProperties applicationProperties, CacheManager cacheManager) {
    var security = applicationProperties.security();

    // withJwkSetUri defers fetching the JWK set until the first token decode, so the
    // app can boot while the auth server is unreachable. fromIssuerLocation eagerly
    // hits /.well-known/openid-configuration and would block startup.
    //
    // The injected Spring Cache lets JwtKeyInvalidationConfig evict the entry by
    // jwkSetUri when a KEY_INVALIDATED event arrives, forcing the next decode to
    // fetch the new JWKS instead of waiting for Nimbus's default 5 min TTL.
    var decoder =
        NimbusJwtDecoder.withJwkSetUri(security.jwkSetUri())
            .cache(Objects.requireNonNull(cacheManager.getCache(CacheProperties.JWKS_CACHE)))
            .build();

    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(security.issuerUri()),
            new JwtAudValidator(security.clientName()),
            new JwtTypValidator(EXPECTED_TYP)));

    return decoder;
  }

  @Bean
  public BearerTokenResolver bearerTokenResolver() {
    var headerResolver = new DefaultBearerTokenResolver();

    return request -> {
      var cookies = request.getCookies();

      if (cookies != null) {
        for (var cookie : cookies) {
          if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
            var value = cookie.getValue();

            if (value != null && !value.isBlank()) {
              return value;
            }
          }
        }
      }

      return headerResolver.resolve(request);
    };
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(toHierarchyPhrase());
  }

  static CorsConfigurationSource createCorsFilter() {
    var config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    var source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }

  private void delegateToHandlerExceptionResolver(
      HttpServletRequest request, HttpServletResponse response, Exception exception) {
    handlerExceptionResolver.resolveException(request, response, null, exception);
  }

  // Baseline shared by every SecurityFilterChain: security headers, CSRF disabled, stateless session, CORS.
  @SneakyThrows
  private static HttpSecurity applyCommonSecurity(HttpSecurity httpSecurity) {
    return httpSecurity
        .headers(
            headersConfigurer ->
                headersConfigurer
                    .xssProtection(
                        xssConfig -> xssConfig.headerValue(HeaderValue.ENABLED_MODE_BLOCK))
                    .contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'")))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(corsConfigurer -> corsConfigurer.configurationSource(createCorsFilter()));
  }

  static void configureAuthorizeHttpRequestCustomizer(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorizeHttpRequestsCustomizer,
      SecurityProperties securityProperties) {
    for (var verbUrl : securityProperties.noAuthenticatedVerbUrls()) {
      authorizeHttpRequestsCustomizer.requestMatchers(verbUrl.method(), verbUrl.url()).permitAll();
    }

    for (var privilegeVerbUrl : securityProperties.highPrivilegeVerbUrls()) {
      authorizeHttpRequestsCustomizer
          .requestMatchers(privilegeVerbUrl.method(), privilegeVerbUrl.url())
          .hasAuthority(ROLE_ADMIN_NAME);
    }

    authorizeHttpRequestsCustomizer.anyRequest().authenticated();
  }

  static String toHierarchyPhrase() {
    var sortedRoles =
        Arrays.stream(UserRole.values())
            .sorted(Comparator.comparingInt(UserRole::superiority).reversed())
            .toList();

    var result = new StringBuilder();

    var size = sortedRoles.size();

    for (var index = 0; index < size; index++) {
      var role = sortedRoles.get(index);

      result.append(role.name());

      if (index < size - 1) {
        result.append(
            role.superiority() == sortedRoles.get(index + 1).superiority() ? " = " : " > ");
      }
    }

    return result.toString();
  }
}
