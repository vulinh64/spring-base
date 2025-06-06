package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import com.vulinh.locale.ServiceErrorCode;
import java.io.Serial;

/**
 * Exception thrown when a request lacks proper authorization or permissions.
 *
 * <p>This exception is raised in scenarios where:
 *
 * <ul>
 *   <li>The request is missing the required authorization header
 *   <li>The provided authorization credentials are invalid
 *   <li>The authenticated user does not have sufficient permissions to access a resource
 * </ul>
 */
public class AuthorizationException extends ApplicationException {

  @Serial private static final long serialVersionUID = -8756921238690096504L;

  /**
   * Creates an {@code AuthorizationException} with a default message and error code.
   *
   * @param args Variable arguments that will be used for message interpolation
   * @return A new {@code AuthorizationException} instance
   */
  public static AuthorizationException invalidAuthorization(Object... args) {
    return invalidAuthorization(
        "Invalid user authorization", ServiceErrorCode.MESSAGE_INVALID_AUTHORIZATION, args);
  }

  /**
   * Creates an {@code AuthorizationException} with a custom message and error code.
   *
   * @param message The custom error message describing the authorization issue
   * @param serviceErrorCode The specific error code related to the authorization failure
   * @param args Variable arguments that will be used for message interpolation
   * @return A new {@code AuthorizationException} instance
   */
  public static AuthorizationException invalidAuthorization(
      String message, ServiceErrorCode serviceErrorCode, Object... args) {
    return invalidAuthorization(message, serviceErrorCode, null, args);
  }

  /**
   * Creates an {@code AuthorizationException} with a custom message, error code, and cause.
   *
   * @param message The custom error message describing the authorization issue
   * @param serviceErrorCode The specific error code related to the authorization failure
   * @param throwable The underlying cause of this exception
   * @param args Variable arguments that will be used for message interpolation
   * @return A new {@code AuthorizationException} instance
   */
  public static AuthorizationException invalidAuthorization(
      String message, ServiceErrorCode serviceErrorCode, Throwable throwable, Object... args) {
    return new AuthorizationException(message, serviceErrorCode, throwable, args);
  }

  /**
   * Constructs a new {@code AuthorizationException} with the specified message, application error,
   * cause and interpolation arguments.
   *
   * @param message The detailed error message describing the authorization issue
   * @param applicationError The specific application error encapsulating the error code and details
   * @param throwable The underlying cause of this exception (can be null)
   * @param args Variable arguments that will be used for message interpolation
   */
  public AuthorizationException(
      String message, ApplicationError applicationError, Throwable throwable, Object... args) {
    super(message, applicationError, throwable, args);
  }
}
