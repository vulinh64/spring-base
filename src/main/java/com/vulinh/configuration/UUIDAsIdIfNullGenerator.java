package com.vulinh.configuration;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(UUIDAsIdIfNullGenerator.UUIDIfNullGeneratorImpl.class)
public @interface UUIDAsIdIfNullGenerator {

  /// Use this strategy only if you want to automatically generate a UUID as the ID for an entity.
  /// This applies when the user leaves the <code>@Id</code> field empty.
  ///
  /// Snippet to use (copy/paste it to the entity's ID field):
  ///
  /// ```java
  /// @GenericGenerator(name = UUIDIfNullStrategy.GENERATOR_NAME, type = UUIDIfNullStrategy.class)
  ///
  /// @GeneratedValue(generator = UUIDIfNullStrategy.GENERATOR_NAME)
  /// ```
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
