package com.vulinh.utils;

import module java.base;

import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.AuthorizationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/*
 * Those methods must be used only after the controller level, where the authentication process has been completed.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  public static Optional<UserBasicResponse> getUserDTO() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken jat)) {
      return Optional.empty();
    }

    var token = jat.getToken();

    var basicResponse =
        UserBasicResponse.builder()
            .id(UUID.fromString(token.getSubject()))
            .username(token.getClaimAsString("preferred_username"))
            .email(token.getClaimAsString("email"))
            .userRoles(
                jat.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet()))
            .build();

    return Optional.of(basicResponse);
  }

  @NonNull
  public static UserBasicResponse getUserDTOOrThrow() {
    return getUserDTO().orElseThrow(AuthorizationException::invalidAuthorization);
  }
}
