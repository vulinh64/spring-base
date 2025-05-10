package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import java.io.Serial;

/**
 * Exception thrown when attempting to create a resource that would conflict with an existing one.
 *
 * <p>This exception is raised in scenarios where:
 *
 * <ul>
 *   <li>A new user is created with a username that already exists
 *   <li>An email address being registered is already associated with another account
 *   <li>A unique identifier or key is duplicated across resources
 *   <li>Any attempt to create a resource violates a uniqueness constraint
 * </ul>
 *
 * <p>This exception corresponds to a <code>409 Conflict</code> HTTP status code in REST APIs and
 * indicates that the request could not be completed due to a conflict with the current state of the
 * target resource.
 */
public class ResourceConflictException extends ApplicationException {

  @Serial private static final long serialVersionUID = -5338307181212324393L;

  /**
   * Creates a {@code ResourceConflictException} with the specified message and error details.
   *
   * @param message The detailed message describing the resource conflict
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation (such as the
   *     conflicting field value)
   * @return A new {@code ResourceConflictException} instance
   */
  public static ResourceConflictException resourceConflictException(
      String message, ApplicationError applicationError, Object... args) {
    return new ResourceConflictException(message, applicationError, args);
  }

  /**
   * Constructs a new {@code ResourceConflictException} with the specified message, error details,
   * and interpolation arguments.
   *
   * @param message The detailed message describing the resource conflict (e.g., "User with email
   *     [email@example.com] already exists")
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation (such as the
   *     conflicting field value)
   */
  public ResourceConflictException(
      String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
