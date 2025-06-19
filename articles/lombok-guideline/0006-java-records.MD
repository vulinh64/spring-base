# Java Records

## Table of Contents

<!-- TOC -->
* [Java Records](#java-records)
  * [Table of Contents](#table-of-contents)
  * [Java Records](#java-records-1)
    * [`@With` Annotation](#with-annotation)
    * [`@Builder` Annotation](#builder-annotation)
  * [Before Java Records in JDK 14](#before-java-records-in-jdk-14)
    * [Example](#example)
<!-- TOC -->

## Java Records

If your project uses JDK 14 or later, consider using **Java Records** to create truly immutable data classes. Records
automatically generate accessors (in fluent style rather than the traditional JavaBeans `getX()` format), along with
implementations for `toString()`, `equals(Object)`, and `hashCode()`.

Most Lombok annotations are not compatible with Java Records, but in most cases, they aren't needed.

One limitation of using Java Records is that Lombok's `@EqualsAndHashCode` annotation does not work with them. As a
result, if you need custom equality or hashing logic, you'll have to manually override the `equals(Object)` and
`hashCode()` methods.

That said, Lombok still offers two annotations that can significantly enhance the capabilities of Java Records: `@With`
and `@Builder`. These annotations can improve immutability handling and object construction, respectively, making
Records even more versatile.

### `@With` Annotation

The `@With` annotation enables the creation of transitional objects using the *wither* pattern. This is especially
useful when working with immutable types like Records, where modifying a single field requires creating a new instance.
For example:

```java
import lombok.With;

import java.util.UUID;

@With
public record Employee(UUID id, String name) {
}

Employee original = new Employee(UUID.randomUUID(), "Jan Žižka");

// Create another employee object whose name is Hussite Wagon
// but same ID as the original object
// The original employee remains intact, obviously
Employee newEmployee = original.withName("Hussite Wagon");
```

### `@Builder` Annotation

The `@Builder` annotation allows you to construct Record instances without having to call the canonical constructor
directly, which can become unwieldy when the record has many fields. With `@Builder`, you get a more readable and
maintainable way to instantiate complex records.

```java
import lombok.Builder;

import java.util.UUID;

@Builder
public record Employee(UUID id, String name) {
}

Employee employee = Employee.builder()
        .id(UUID.randomUUID())
        .name("Jan Žižka")
        .build();

```

Think of Java Records as "named" tuples in other languages like Python or JavaScript. Java is still a static typing
language, so everything has to associate with a certain type.

## Before Java Records in JDK 14

If your projects use JDK version below 14, but you wanted to create a "data class", something that other languages like
Kotlin or C# have done very successfully for years, you can opt for using `@Value` annotation.

### Example

```java
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class EmployeeData {
  UUID id;
  String name;
}
```

A "value" class in Java is:

* By default, all fields are `final` and `private`. Which means completely immutable. To help creating transitional
  objects, you can opt for using `@With`. To help creating objects, you can, as always, make use of `@Builder`.

---

* Because all fields are `final`, no `@Setter` is present in the class. However, `@Getter` is present.

---

* Implicitly contains those annotations: `@ToString`, `@EqualsAndHashCode`, `@AllArgsConstructor`,
  `@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)`, and `@Getter`.