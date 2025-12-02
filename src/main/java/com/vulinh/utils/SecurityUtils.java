package com.vulinh.utils;

import module java.base;

import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.AuthorizationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/*
 * Those methods must be used only after the controller level, where the authentication process has been completed.
 *
 * Also, this cannot also run on @Async methods or classes, because those run on different threads, and thus the
 * SecurityContextHolder will not have the authentication information (will return null).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SecurityUtils {

  public static Optional<UserBasicResponse> getUserDTO() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken jat)) {
      log.warn(
          "Unexpected Authentication type: {} | Required type: {}",
          Optional.ofNullable(authentication)
              .map(Object::getClass)
              .map(Class::getName)
              .orElse(null),
          JwtAuthenticationToken.class.getName());

      return Optional.empty();
    }

    var token = jat.getToken();

    return Optional.of(
        UserBasicResponse.builder()
            .id(UUID.fromString(token.getSubject()))
            .username(token.getClaimAsString("preferred_username"))
            .email(token.getClaimAsString("email"))
            .userRoles(
                jat.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet()))
            .build());
  }

  @NonNull
  public static UserBasicResponse getUserDTOOrThrow() {
    return getUserDTO().orElseThrow(AuthorizationException::invalidAuthorization);
  }
}
