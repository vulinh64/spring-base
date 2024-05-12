package com.vulinh.filter;

import com.vulinh.data.dto.security.CustomAuthentication;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.user.UserService;
import com.vulinh.utils.security.JwtValidationUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final HandlerExceptionResolver handlerExceptionResolver;
  private final JwtValidationUtils jwtValidationUtils;

  private final UserService userService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) {
    try {
      Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
          .map(JwtFilter::parseBearerToken)
          .map(jwtValidationUtils::validate)
          .map(
              jwtPayload ->
                  CustomAuthentication.of(
                      jwtPayload,
                      userService
                          .findByIdAndIsActiveIsTrue(jwtPayload.id())
                          .orElseThrow(ExceptionBuilder::invalidAuthorization),
                      request))
          .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

      filterChain.doFilter(request, response);
    } catch (Exception exception) {
      handlerExceptionResolver.resolveException(request, response, null, exception);
    }
  }

  private static String parseBearerToken(String token) {
    return token.startsWith("Bearer") ? token.substring(7) : token;
  }
}
