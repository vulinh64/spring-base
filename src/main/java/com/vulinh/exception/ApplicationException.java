package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import java.io.Serial;
import lombok.Getter;
import org.springframework.lang.NonNull;

/**
 * Abstract base class for all application-specific exceptions.
 *
 * <p>This class serves as the root of the application's exception hierarchy. It encapsulates an
 * {@link ApplicationError} that provides error code and localized message capabilities.
 *
 * <p>Note that the message provided to the constructor (accessible via {@link #getMessage()}) is
 * intended for logging and debugging purposes only and should not be exposed to clients.
 * Client-facing error messages should be obtained through the associated {@link ApplicationError}.
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

  @Serial private static final long serialVersionUID = -1281113760218362867L;

  /**
   * The application error associated with this exception. This object provides error code and
   * localized display message capabilities.
   */
  private final transient ApplicationError applicationError;

  /**
   * Arguments to be used for message interpolation when generating display messages. These values
   * are passed to {@link ApplicationError#getDisplayMessage(Object...)}.
   */
  private final transient Object[] args;

  /**
   * Constructs a new application exception with the specified details.
   *
   * @param message The error message for logging purposes - not to be displayed to clients
   * @param applicationError The application error object providing error code and display message
   * @param throwable The cause of this exception (maybe null)
   * @param args Optional arguments for message interpolation in display messages
   * @throws NullPointerException if applicationError is null
   */
  protected ApplicationException(
      String message,
      @NonNull ApplicationError applicationError,
      Throwable throwable,
      Object... args) {
    super(message, throwable);
    this.applicationError = applicationError;
    this.args = args;
  }
}
