package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;

/// Exception thrown when security configuration issues are detected in the application.
public class SecurityConfigurationException extends ApplicationException {

  @Serial private static final long serialVersionUID = 7368928143863136668L;

  /// Creates a [SecurityConfigurationException] with the specified message and error details.
  ///
  /// @param message The detailed message describing the security configuration issue
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation
  /// @return A new `SecurityConfigurationException` instance
  public static SecurityConfigurationException configurationException(
      String message, ApplicationError applicationError, Object... args) {
    return configurationException(message, applicationError, null, args);
  }

  /// Creates a [SecurityConfigurationException] with the specified message, error details, and
  /// an underlying cause.
  ///
  /// @param message The detailed message describing the security configuration issue
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param throwable The underlying cause of this exception (can be null)
  /// @param args Variable arguments that will be used for message interpolation
  /// @return A new `SecurityConfigurationException` instance
  public static SecurityConfigurationException configurationException(
      String message, ApplicationError applicationError, Throwable throwable, Object... args) {
    return new SecurityConfigurationException(message, applicationError, throwable, args);
  }

  /// Constructs a new [SecurityConfigurationException] with the specified message, error details, cause, and
  /// interpolation arguments.
  ///
  /// @param message The detailed message describing the security configuration issue
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param throwable The underlying cause of this exception (can be null)
  /// @param args Variable arguments that will be used for message interpolation
  public SecurityConfigurationException(
      String message, ApplicationError applicationError, Throwable throwable, Object... args) {
    super(message, applicationError, throwable, args);
  }
}
