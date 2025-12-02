package com.vulinh.exception;

import module java.base;

import com.vulinh.data.base.ApplicationError;
import com.vulinh.locale.ServiceErrorCode;

/// Exception thrown when an entity cannot be found by the provided identifier.
///
/// This exception is raised in scenarios where:
///
/// - A database query fails to retrieve an entity with the specified ID
///
/// - An attempt is made to access or manipulate a non-existent resource
///
/// - A referenced entity has been deleted or is otherwise unavailable
public class NotFound404Exception extends ApplicationException {

  @Serial private static final long serialVersionUID = 5815209580420516331L;

  /// Creates a [NotFound404Exception] with a formatted message indicating which entity could not
  /// be found by its ID.
  ///
  /// @param entityName The name of the entity type that was being searched for (e.g., "Post", "Comment")
  /// @param entityId The identifier used to search for the entity
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @return A new [NotFound404Exception] instance with a formatted message
  public static NotFound404Exception entityNotFound(
      String entityName, Object entityId, ApplicationError applicationError) {
    return new NotFound404Exception(
        "Entity [%s] with ID [%s] not found".formatted(entityName, entityId),
        applicationError,
        entityName);
  }

  /// Creates a `NotFoundException` for an invalid Keycloak user.
  ///
  /// @param userId The Keycloak user ID that was not found
  /// @param e The exception that was thrown during lookup
  /// @return A new `NotFoundException` instance for the invalid user
  public static NotFound404Exception invalidKeycloakUser(UUID userId, Throwable e) {
    return new NotFound404Exception(
        "Keycloak user with ID [%s] not found".formatted(userId),
        ServiceErrorCode.MESSAGE_INVALID_USER,
        e);
  }

  /// Constructs a new [NotFound404Exception] with the specified message, error details, and interpolation arguments.
  ///
  /// @param message The detailed message describing which entity was not found
  /// @param applicationError The specific application error encapsulating the error code and details
  /// @param args Variable arguments that will be used for message interpolation
  NotFound404Exception(String message, ApplicationError applicationError, Object... args) {
    this(message, applicationError, null, args);
  }

  /**
   * Constructs a new {@code NotFoundException} with the specified message, error details, cause, and interpolation arguments.
   *
   * @param message The detailed message describing which entity was not found
   * @param applicationError The specific application error encapsulating the error code and details
   * @param throwable The cause of this exception
   * @param args Variable arguments that will be used for message interpolation
   */
  NotFound404Exception(String message, ApplicationError applicationError, Throwable throwable, Object... args) {
    super(message, applicationError, throwable, args);
  }
}
