package com.vulinh.service.auth;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.keycloak.KeycloakAuthExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  static final String GRANT_TYPE_DEFAULT = "password";

  final KeycloakAuthExchange keycloakAuthExchange;

  final ApplicationProperties applicationProperties;

  /// @deprecated Dedicate to KeyCloak
  @Deprecated(forRemoval = true)
  public TokenResponse login(UserLoginRequest userLoginRequest) {
    var keycloakResponse =
        keycloakAuthExchange.getToken(
            GRANT_TYPE_DEFAULT,
            applicationProperties.security().clientName(),
            userLoginRequest.username(),
            userLoginRequest.password());

    return new TokenResponse(keycloakResponse.accessToken(), keycloakResponse.refreshToken());
  }
}
