package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;

/// Exception thrown when a user attempts to access resources without proper permissions.
///
/// This exception is raised in scenarios where:
///
/// - A user attempts to access resources owned by another user
///
/// - A user tries to perform operations requiring elevated privileges
///
/// - Access is attempted to a protected resource without the required permission level
///
/// Only superusers with overriding permissions would typically be allowed to perform the operations that trigger this
/// exception for regular users.
public class NoSuchPermissionException extends ApplicationException {

  @Serial private static final long serialVersionUID = 8590613467771448158L;

  /// Creates a [NoSuchPermissionException] with the specified message and error details.
  ///
  /// @param message The detailed message describing the permission violation
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation
  /// @return A new [NoSuchPermissionException] instance
  public static NoSuchPermissionException noSuchPermissionException(
      String message, ApplicationError applicationError, Object... args) {
    return new NoSuchPermissionException(message, applicationError, args);
  }

  /// Constructs a new [NoSuchPermissionException] with the specified message, error details,
  /// and interpolation arguments.
  ///
  /// @param message The detailed message describing the permission violation
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation
  NoSuchPermissionException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
