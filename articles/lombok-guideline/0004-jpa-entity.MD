# JPA Entities

## Table of Contents

<!-- TOC -->
* [JPA Entities](#jpa-entities)
  * [Table of Contents](#table-of-contents)
  * [Basic JPA Entity](#basic-jpa-entity)
  * [Explanation](#explanation)
  * [Why No `@EqualsAndHashCode`?](#why-no-equalsandhashcode)
  * [Recommended `boolean equals(Object)` and `int hashCode()` Implementations](#recommended-boolean-equalsobject-and-int-hashcode-implementations)
<!-- TOC -->

## Basic JPA Entity

The recommended Lombok annotations for JPA entities is as following:

```java
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Accessors(chain = true)
public class EmployeeEntity {

  // basic fields

  @ToString.Exclude
  @OneToMany(mappedBy = "employee")
  private Set<Task> tasks;

  @ToString.Exclude
  @ManyToOne
  private Department department;
}
```

## Explanation

* Basic usage of `@Getter` and `@Setter` is highly recommended, especially since many frameworks rely on the Reflection
  API to access fields and methods.

---

* JPA entities require a no-argument constructor. By default, compiler generates one if no other constructors are
  defined. However, when an all-arguments constructor (e.g., via `@AllArgsConstructor`) is present, it's necessary to
  explicitly add `@NoArgsConstructor` to ensure the presence of both constructors.

---

* The `@AllArgsConstructor` annotation is required when using `@Builder`, as the builder pattern relies on the
  availability of a constructor that accepts all fields. `@Builder` is a powerful Lombok annotation for implementing the
  builder creational pattern, particularly useful for constructing complex JPA entities. This annotation did not work
  very well with subclasses (required the usage of experimental `@SuperBuilder`), however, so pay attention (and limit 
  the usage of inheritance in this case).

---

* When using `@ToString`, always exclude fields involved in JPA relationships, such as those annotated with
  `@OneToMany`, `@OneToOne`, `@ManyToOne`, or `@ManyToMany`, to avoid potential `StackOverflowError` caused by
  bidirectional or cyclic entity relationships. Use `@ToString.Exclude` for this specified purpose.

    * This exclusion principle also applies to any object with mutual or cyclic dependencies.

---

* `@Accessors(chain = true)` enables chainable setter methods, allowing calls like `entity.setA(a).setB(b)`, which
  enhances fluency and readability in object configuration.

## Why No `@EqualsAndHashCode`?

For optimal performance, JPA entities require custom implementation of `boolean equals(Object)` and `int hashCode()`.
Therefore, we should not be using `@EqualsAndHashCod` most of the time for JPA entities.

> You can check the following article for the optimal implementation of those two methods:
>
> https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/

## Recommended `boolean equals(Object)` and `int hashCode()` Implementations

Below is the recommended implementation of both methods:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
public class EmployeeEntity {

  @Id
  private UUID id;

  // other fields omitted for brevity

  @Override
  public final boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (id == null) {
      return false;
    }

    // Requires JDK 16+
    return other instanceof EmployeeEntity that
            && getEffectiveClass(this) == getEffectiveClass(that)
            && Objects.equals(id, that.getId());
  }

  @Override
  public final int hashCode() {
    var id = getId();

    return id == null ? getEffectiveClass(this).hashCode() : id.hashCode();
  }

  // Requires Hibernate 6+
  // Hibernate version below 6 requires different approaches
  private static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy hibernateProxy
            ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
            : object.getClass();
  }
}

```

You can also extract both methods into an abstract base class, which other concrete JPA entities can extend, like this:

```java
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

public abstract class AbstractIdentifiable<I> {

  public abstract I getId();

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

    return id == null
            ? getEffectiveClass(this).hashCode()
            : id.hashCode();
  }

  // This can be used to check the actual underlying class of entities
  private static Class<?> getEffectiveClass(Object object) {
    return object instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : object.getClass();
  }
}
```