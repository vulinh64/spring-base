package com.vulinh.data.entity;

import com.vulinh.utils.Identifiable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

// https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
public abstract class AbstractIdentifiable implements Identifiable<Serializable>, Serializable {

  @Serial private static final long serialVersionUID = 6946378021605529170L;

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
    return id != null
        && other instanceof AbstractIdentifiable that
        && Objects.equals(id, that.getId());
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
