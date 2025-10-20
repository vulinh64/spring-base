package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;
import com.vulinh.locale.ServiceErrorCode;

/// Exception thrown when a user's authentication attempt fails due to invalid credentials.
///
/// This exception is typically thrown during login processes when provided credentials (such as
/// username/password or tokens) do not match expected values in the system. It extends
/// [ApplicationException] and returns an HTTP 401 UNAUTHORIZED status code.
public class InvalidCredentialsException extends ApplicationException {

  @Serial private static final long serialVersionUID = -4482845426503935711L;

  /// Creates a new [InvalidCredentialsException] with a predefined message and error code.
  ///
  /// @param args Variable arguments that will be used for message formatting
  /// @return A new [InvalidCredentialsException] instance with default error message
  public static InvalidCredentialsException invalidCredentialsException(Object... args) {
    return new InvalidCredentialsException(
        "Invalid user credentials", ServiceErrorCode.MESSAGE_INVALID_CREDENTIALS, args);
  }

  /// Constructs a new [InvalidCredentialsException] with specified error details.
  ///
  /// @param message The detail message describing the error
  /// @param applicationError The application-specific error code
  /// @param args Variable arguments that will be used for message formatting
  InvalidCredentialsException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
