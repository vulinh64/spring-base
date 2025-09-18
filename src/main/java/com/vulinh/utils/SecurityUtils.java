package com.vulinh.utils;

import module java.base;

import com.vulinh.configuration.CustomAuthentication;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.AuthorizationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  public static Optional<UserBasicResponse> getUserDTO() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .filter(CustomAuthentication.class::isInstance)
        .map(CustomAuthentication.class::cast)
        .map(CustomAuthentication::getPrincipal);
  }

  @NonNull
  public static UserBasicResponse getUserDTOOrThrow() {
    return getUserDTO().orElseThrow(AuthorizationException::invalidAuthorization);
  }
}
