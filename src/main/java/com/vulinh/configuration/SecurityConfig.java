package com.vulinh.configuration;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.UserRole;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.filter.JwtFilter;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    var roleHierarchy = new RoleHierarchyImpl();

    roleHierarchy.setHierarchy(UserRole.toHierarchyPhrase());

    return roleHierarchy;
  }

  private void configureAuthorizeHttpRequestCustomizer(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorizeHttpRequestsCustomizer) {
    for (var verbUrl : securityConfigProperties.verbUrls()) {
      authorizeHttpRequestsCustomizer.requestMatchers(verbUrl.method(), verbUrl.url()).permitAll();
    }

    authorizeHttpRequestsCustomizer
        .requestMatchers(securityConfigProperties.noAuthenticatedUrls().toArray(String[]::new))
        .permitAll()
        .requestMatchers(EndpointConstant.getUrlsWithPrivilege(ROLE_ADMIN))
        .hasAuthority(ROLE_ADMIN.name())
        .anyRequest()
        .authenticated();
  }

  private void handleSecurityException(
      HttpServletRequest request, HttpServletResponse response, Throwable authException) {
    var userDTO = SecurityUtils.getUserDTO(request);

    log.info(
        "User {{ User ID = {}; Unique ID = {}; Roles = {} }} accessed resource [{} {}] failed",
        userDTO.map(UserBasicDTO::username).orElse("Unknown"),
        userDTO.map(UserBasicDTO::id).orElse("Invalid ID"),
        userDTO.map(UserBasicDTO::userRoles).stream().flatMap(Collection::stream).toList(),
        request.getMethod(),
        request.getRequestURI());

    handlerExceptionResolver.resolveException(
        request, response, null, ExceptionBuilder.invalidAuthorization(authException));
  }
}
