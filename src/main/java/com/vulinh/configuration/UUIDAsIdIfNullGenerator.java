package com.vulinh.configuration;

import com.vulinh.data.constant.CommonConstant;
import java.io.Serial;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(UUIDAsIdIfNullGenerator.UUIDIfNullGeneratorImpl.class)
public @interface UUIDAsIdIfNullGenerator {

  /**
   * Use this strategy only if you want to automatically generate a UUID as the ID for an entity.
   * This applies when the user leaves the <code>@Id</code> field empty.
   *
   * <p>Snippet to use (copy/paste it to the entity's ID field):
   *
   * <pre>{@code
   * @GenericGenerator(name = UUIDIfNullStrategy.GENERATOR_NAME, type = UUIDIfNullStrategy.class)
   * @GeneratedValue(generator = UUIDIfNullStrategy.GENERATOR_NAME)
   * }</pre>
   */
  class UUIDIfNullGeneratorImpl implements IdentifierGenerator {

    @Serial private static final long serialVersionUID = -8834326837580306821L;

    @Override
    public UUID generate(SharedSessionContractImplementor session, Object entity) {
      var entityPersister = session.getEntityPersister(entity.getClass().getName(), entity);

      return Optional.of(entityPersister)
          .map(persist -> persist.getIdentifier(entity, session))
          .map(UUID.class::cast)
          .orElseGet(UUIDIfNullGeneratorImpl::tryGeneratingUUID);
    }

    // Chance of generating 00000000-0000-0000-000000000000 is even lower
    // than the chance you can have a girlfriend, lmao
    static UUID tryGeneratingUUID() {
      while (true) {
        var result = UUID.randomUUID();

        if (!CommonConstant.NIL_UUID.equals(result)) {
          return result;
        }
      }
    }
  }
}
