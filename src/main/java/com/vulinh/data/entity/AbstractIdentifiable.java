package com.vulinh.data.entity;

import com.vulinh.utils.Identifiable;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.proxy.HibernateProxy;

// https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
public abstract class AbstractIdentifiable<I> implements Identifiable<I> {

  @Serial private static final long serialVersionUID = 6946378021605529170L;

  // Every JPA entity MUST extend this class to use equals and hashCode!!!
  public abstract static class UUIDJpaEntity extends AbstractIdentifiable<UUID> {

    @Serial private static final long serialVersionUID = -7023656451162094548L;
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null) {
      return false;
    }

    if (getEffectiveClass(other) != getEffectiveClass(this)) {
      return false;
    }

    var id = getId();

    // Entity without ID will always be different
    return id != null && other instanceof AbstractIdentifiable<?> that && id.equals(that.getId());
  }

  @Override
  public final int hashCode() {
    var id = getId();

    if (id != null) {
      return Objects.hashCode(id);
    }

    return getEffectiveClass(this).hashCode();
  }

  private static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : object.getClass();
  }
}
