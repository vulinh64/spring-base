package com.vulinh.exception;

import com.vulinh.data.base.ApplicationError;
import java.io.Serial;

/**
 * Exception thrown when an entity cannot be found by the provided identifier.
 *
 * <p>This exception is raised in scenarios where:
 *
 * <ul>
 *   <li>A database query fails to retrieve an entity with the specified ID
 *   <li>An attempt is made to access or manipulate a non-existent resource
 *   <li>A referenced entity has been deleted or is otherwise unavailable
 * </ul>
 */
public class NotFoundException extends ApplicationException {

  @Serial private static final long serialVersionUID = 5815209580420516331L;

  /**
   * Creates a {@code NotFoundException} with a formatted message indicating which entity could not
   * be found by its ID.
   *
   * @param entityName The name of the entity type that was being searched for (e.g., "Post",
   *     "Comment")
   * @param entityId The identifier used to search for the entity
   * @param applicationError The specific application error encapsulating the error code and details
   * @return A new {@code NotFoundException} instance with a formatted message
   */
  public static NotFoundException entityNotFound(
      String entityName, Object entityId, ApplicationError applicationError) {
    return new NotFoundException(
        "Entity [%s] with ID [%s] not found".formatted(entityName, entityId),
        applicationError,
        entityName);
  }

  /**
   * Constructs a new {@code NotFoundException} with the specified message, error details, and
   * interpolation arguments.
   *
   * @param message The detailed message describing which entity was not found
   * @param applicationError The specific application error encapsulating the error code and details
   * @param args Variable arguments that will be used for message interpolation
   */
  public NotFoundException(String message, ApplicationError applicationError, Object... args) {
    super(message, applicationError, null, args);
  }
}
