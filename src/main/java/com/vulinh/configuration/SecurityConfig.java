package com.vulinh.configuration;

import com.vulinh.constant.UserRole;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.filter.JwtFilter;
import com.vulinh.utils.SecurityUrlUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityConfigProperties.class)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

  private static final UserRole ROLE_ADMIN = UserRole.ADMIN;

  private final SecurityConfigProperties securityConfigProperties;

  private final JwtFilter jwtFilter;

  private final HandlerExceptionResolver handlerExceptionResolver;

  @Bean
  @SneakyThrows
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(
            sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(this::configureAuthorizeHttpRequestCustomizer)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptionHandlingCustomizer ->
                exceptionHandlingCustomizer
                    .accessDeniedHandler(this::handleSecurityException)
                    .authenticationEntryPoint(this::handleSecurityException))
        .build();
  }

  @Bean
  public CorsFilter corsFilter() {
    var config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    var source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(UserRole.toHierarchyPhrase());
  }

  private void configureAuthorizeHttpRequestCustomizer(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorizeHttpRequestsCustomizer) {
    for (var verbUrl : securityConfigProperties.verbUrls()) {
      authorizeHttpRequestsCustomizer.requestMatchers(verbUrl.method(), verbUrl.url()).permitAll();
    }

    for (var privilegeVerbUrl : SecurityUrlUtils.getVerbUrlsWithPrivilege(ROLE_ADMIN)) {
      authorizeHttpRequestsCustomizer
          .requestMatchers(privilegeVerbUrl.method(), privilegeVerbUrl.url())
          .hasAuthority(ROLE_ADMIN.name());
    }

    authorizeHttpRequestsCustomizer
        .requestMatchers(securityConfigProperties.noAuthenticatedUrls().toArray(String[]::new))
        .permitAll()
        .requestMatchers(SecurityUrlUtils.getUrlsWithPrivilege(ROLE_ADMIN))
        .hasAuthority(ROLE_ADMIN.name())
        .anyRequest()
        .authenticated();
  }

  private void handleSecurityException(
      HttpServletRequest request, HttpServletResponse response, Throwable authException) {
    handlerExceptionResolver.resolveException(
        request, response, null, ExceptionBuilder.invalidAuthorization(authException));
  }
}
