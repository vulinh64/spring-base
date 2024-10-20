package com.vulinh.filter;

import com.vulinh.configuration.CustomAuthenticationManager;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.utils.security.AccessTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final HandlerExceptionResolver handlerExceptionResolver;
  private final AccessTokenValidator accessTokenValidator;
  private final CustomAuthenticationManager customAuthenticationManager;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) {
    try {
      Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
          .map(JwtFilter::parseBearerToken)
          .map(accessTokenValidator::validateAccessToken)
          .map(JwtFilter::getPreAuthenticatedAuthenticationToken)
          .map(PreAuthenticatedAuthenticationToken::getPrincipal)
          .filter(JwtPayload.class::isInstance)
          .map(JwtPayload.class::cast)
          .map(JwtFilter::getPreAuthenticatedAuthenticationToken)
          .map(customAuthenticationManager::authenticate)
          .map(
              authentication ->
                  authentication.addDetails(
                      new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
                          request, authentication.getAuthorities())))
          .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

      filterChain.doFilter(request, response);
    } catch (Exception exception) {
      handlerExceptionResolver.resolveException(request, response, null, exception);
    }
  }

  private static String parseBearerToken(String token) {
    return token.startsWith("Bearer") ? token.substring(7) : token;
  }

  private static PreAuthenticatedAuthenticationToken getPreAuthenticatedAuthenticationToken(
      JwtPayload jwtPayload) {
    return new PreAuthenticatedAuthenticationToken(jwtPayload, null);
  }
}
