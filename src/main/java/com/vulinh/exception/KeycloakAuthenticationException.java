package com.vulinh.exception;

import com.vulinh.locale.ServiceErrorCode;
import java.io.Serial;

/// When authentication with Keycloak failed
public class KeycloakAuthenticationException extends ApplicationException {

  @Serial private static final long serialVersionUID = 0L;

  public KeycloakAuthenticationException(String username, Throwable throwable) {
    super(
        "Authentication for user %s failed: %s".formatted(username, throwable.getMessage()),
        ServiceErrorCode.MESSAGE_INVALID_USER,
        throwable);
  }
}
