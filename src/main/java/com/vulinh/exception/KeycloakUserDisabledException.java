package com.vulinh.exception;

import module java.base;

import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.ApplicationError;

/// The exception to represent the state when a Keycloak user has been disabled
public class KeycloakUserDisabledException extends ApplicationException {

  @Serial private static final long serialVersionUID = 4705314241370018291L;

  public static KeycloakUserDisabledException userDisabledException(UUID userId) {
    return new KeycloakUserDisabledException(
        "User with ID [%s] has been disabled".formatted(userId),
        ServiceErrorCode.MESSAGE_USER_DISABLED);
  }

  KeycloakUserDisabledException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
