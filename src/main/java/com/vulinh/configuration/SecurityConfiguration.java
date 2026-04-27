package com.vulinh.configuration;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.configuration.data.ApplicationProperties.SecurityProperties;
import com.vulinh.data.constant.UserRole;
import com.vulinh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

  @Bean
  @SneakyThrows
  public SecurityFilterChain securityFilterChain(
      HttpSecurity httpSecurity, ApplicationProperties applicationProperties) {
    var security = applicationProperties.security();

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
        .authorizeHttpRequests(
            authorizeHttpRequestsCustomizer ->
                configureAuthorizeHttpRequestCustomizer(authorizeHttpRequestsCustomizer, security))
        .cors(corsConfigurer -> corsConfigurer.configurationSource(createCorsFilter()))
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
                                jwt ->
                                    JwtUtils.parseAuthoritiesByCustomClaims(
                                        jwt, security.clientName()))))
        .build();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(toHierarchyPhrase());
  }

  CorsConfigurationSource createCorsFilter() {
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

    authorizeHttpRequestsCustomizer
        .requestMatchers(securityProperties.noAuthenticatedUrls().toArray(String[]::new))
        .permitAll()
        .anyRequest()
        .authenticated();
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
