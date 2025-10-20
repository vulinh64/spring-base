package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;

/// Exception thrown when data fails to meet business validation rules.
///
/// This exception is raised in scenarios where input data violates domain-specific validation rules, such as:
///
/// - Required fields being empty or null
///
/// - String length exceeding maximum allowed limits
///
/// - Values falling outside acceptable ranges
///
/// - Data format violations (e.g., invalid email format, phone number format)
///
/// - Business rule violations (e.g., end date before start date)
///
/// - Input data not conforming to expected patterns or structures
///
/// This exception corresponds to a <code>400 Bad Request</code> HTTP status code in REST APIs and indicates that the
/// client-provided data could not be processed due to validation failures.
public class ValidationException extends ApplicationException {

  @Serial private static final long serialVersionUID = -1320674528455566605L;

  /// Creates a [ValidationException] with the specified message and error details.
  ///
  /// @param message The detailed message describing the validation failure
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation (such as field names, invalid values,
  /// or limits)
  /// @return A new [ValidationException] instance
  public static ValidationException validationException(
      String message, ApplicationError applicationError, Object... args) {
    return new ValidationException(message, applicationError, null, args);
  }

  /// Constructs a new [ValidationException] with the specified message, error details, and interpolation arguments.
  ///
  /// @param message The detailed message describing the validation failure (e.g., "Field name cannot be empty")
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation (such as field names, invalid values,
  /// or limits)
  ValidationException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
