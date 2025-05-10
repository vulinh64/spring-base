package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import java.io.Serial;

/**
 * Exception thrown when data fails to meet business validation rules.
 *
 * <p>This exception is raised in scenarios where input data violates domain-specific validation
 * rules, such as:
 *
 * <ul>
 *   <li>Required fields being empty or null
 *   <li>String length exceeding maximum allowed limits
 *   <li>Values falling outside acceptable ranges
 *   <li>Data format violations (e.g., invalid email format, phone number format)
 *   <li>Business rule violations (e.g., end date before start date)
 *   <li>Input data not conforming to expected patterns or structures
 * </ul>
 *
 * <p>This exception corresponds to a <code>400 Bad Request</code> HTTP status code in REST APIs and
 * indicates that the client-provided data could not be processed due to validation failures.
 */
public class ValidationException extends ApplicationException {

  @Serial private static final long serialVersionUID = -1320674528455566605L;

  /**
   * Creates a {@code ValidationException} with the specified message and error details.
   *
   * @param message The detailed message describing the validation failure
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation (such as field
   *     names, invalid values, or limits)
   * @return A new {@code ValidationException} instance
   */
  public static ValidationException validationException(
      String message, ApplicationError applicationError, Object... args) {
    return new ValidationException(message, applicationError, null, args);
  }

  /**
   * Constructs a new {@code ValidationException} with the specified message, error details, and
   * interpolation arguments.
   *
   * @param message The detailed message describing the validation failure (e.g., "Field [name]
   *     cannot be empty")
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation (such as field
   *     names, invalid values, or limits)
   */
  public ValidationException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
