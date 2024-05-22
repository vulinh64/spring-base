package com.vulinh.configuration;

import java.io.Serial;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * Only use this strategy if you only need to automatically generate a UUID when you did not
 * explicitly provide one manually.
 *
 * <p>Snippet to use (copy/paste it to the entity's ID field):
 *
 * <pre>{@code
 * @GenericGenerator(name = UUIDIfNullStrategy.GENERATOR_NAME, type = UUIDIfNullStrategy.class)
 * @GeneratedValue(generator = UUIDIfNullStrategy.GENERATOR_NAME)
 * }</pre>
 */
public class UUIDIfNullStrategy implements IdentifierGenerator {

  // It has to be a string literal
  public static final String GENERATOR_NAME = "UUIDIfNullStrategy";

  @Serial private static final long serialVersionUID = -8834326837580306821L;

  @Override
  public String generate(SharedSessionContractImplementor session, Object entity) {
    return Optional.of(session.getEntityPersister(entity.getClass().getName(), entity))
        .map(entityPersister -> entityPersister.getIdentifier(entity, session))
        .map(String::valueOf)
        .orElseGet(() -> UUID.randomUUID().toString());
  }
}
