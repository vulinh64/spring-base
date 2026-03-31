package com.vulinh.exception;

import module java.base;

import com.vulinh.locale.ServiceErrorCode;

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
