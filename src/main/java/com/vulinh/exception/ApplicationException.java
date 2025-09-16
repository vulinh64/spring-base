package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;
import lombok.Getter;
import org.springframework.lang.NonNull;

/// Abstract base class for all application-specific exceptions.
///
/// This class serves as the root of the application's exception hierarchy. It encapsulates an[ApplicationError] that
/// provides error code and localized message capabilities.
///
/// Note that the message provided to the constructor (accessible via [#getMessage()]) is intended for logging and
/// debugging purposes only and should not be exposed to clients. Client-facing error messages should be obtained
/// through the associated [ApplicationError].
@Getter
public abstract class ApplicationException extends RuntimeException {

  @Serial private static final long serialVersionUID = -1281113760218362867L;

  /// The application error associated with this exception. This object provides error code and localized display
  /// message capabilities.
  private final transient ApplicationError applicationError;

  /// Arguments to be used for message interpolation when generating display messages.
  private final transient Object[] args;

  /// Constructs a new application exception with the specified details.
  ///
  /// @param message The error message for logging purposes - not to be displayed to clients
  /// @param applicationError The application error object providing error code and display message
  /// @param throwable The cause of this exception (maybe null)
  /// @param args Optional arguments for message interpolation in display messages
  /// @throws NullPointerException if applicationError is null
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
