package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import java.io.Serial;

/**
 * Exception thrown when a user attempts to access resources without proper permissions.
 *
 * <p>This exception is raised in scenarios where:
 *
 * <ul>
 *   <li>A user attempts to access resources owned by another user
 *   <li>A user tries to perform operations requiring elevated privileges
 *   <li>Access is attempted to a protected resource without the required permission level
 * </ul>
 *
 * <p>Only superusers with overriding permissions would typically be allowed to perform the
 * operations that trigger this exception for regular users.
 */
public class NoSuchPermissionException extends ApplicationException {

  @Serial private static final long serialVersionUID = 8590613467771448158L;

  /**
   * Creates a {@code NoSuchPermissionException} with the specified message and error details.
   *
   * @param message The detailed message describing the permission violation
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation
   * @return A new {@code NoSuchPermissionException} instance
   */
  public static NoSuchPermissionException noSuchPermissionException(
      String message, ApplicationError applicationError, Object... args) {
    return new NoSuchPermissionException(message, applicationError, args);
  }

  /**
   * Constructs a new {@code NoSuchPermissionException} with the specified message, error details,
   * and interpolation arguments.
   *
   * @param message The detailed message describing the permission violation
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation
   */
  public NoSuchPermissionException(
      String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
