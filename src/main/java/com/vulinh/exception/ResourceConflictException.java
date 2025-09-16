package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;

/// Exception thrown when attempting to create a resource that would conflict with an existing one.
///
/// This exception is raised in scenarios where:
///
/// - A new user is created with a username that already exists
///
/// - An email address being registered is already associated with another account
///
/// - A unique identifier or key is duplicated across resources
///
/// - Any attempt to create a resource violates a uniqueness constraint
///
/// This exception corresponds to a <code>409 Conflict</code> HTTP status code in REST APIs and indicates that the
/// request could not be completed due to a conflict with the current state of the target resource.
public class ResourceConflictException extends ApplicationException {

  @Serial private static final long serialVersionUID = -5338307181212324393L;

  /// Creates a [ResourceConflictException] with the specified message and error details.
  ///
  /// @param message The detailed message describing the resource conflict
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation (such as the conflicting field value)
  /// @return A new [ResourceConflictException] instance
  public static ResourceConflictException resourceConflictException(
      String message, ApplicationError applicationError, Object... args) {
    return new ResourceConflictException(message, applicationError, args);
  }

  /// Constructs a new [ResourceConflictException] with the specified message, error details, and interpolation
  /// arguments.
  ///
  /// @param message The detailed message describing the resource conflict (e.g., "User with email 'email@example.com'
  /// already exists")
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation (such as the conflicting field value)
  public ResourceConflictException(
      String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
