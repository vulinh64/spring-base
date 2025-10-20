package com.vulinh.data.base;

import module java.base;

import org.hibernate.proxy.HibernateProxy;

// https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
// I changed hashCode calculation to use entity ID when
// available instead of effective class-based hash
// Every JPA entity MUST extend this class if wanted to use equals and hashCode!!!
public abstract class AbstractIdentifiable<I> implements Identifiable<I> {

  @Serial private static final long serialVersionUID = 6946378021605529170L;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    var id = getId();

    if (id == null) {
      return false;
    }

    return o instanceof AbstractIdentifiable<?> other
        && getEffectiveClass(this) == getEffectiveClass(o)
        && Objects.equals(id, other.getId());
  }

  @Override
  public final int hashCode() {
    var id = getId();

    return id == null ? getEffectiveClass(this).hashCode() : id.hashCode();
  }

  static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : object.getClass();
  }
}
