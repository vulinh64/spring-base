# Lombok Introduction

## Table of Contents

<!-- TOC -->
* [Lombok Introduction](#lombok-introduction)
  * [Table of Contents](#table-of-contents)
  * [1. You Can Safely Use Lombok Annotations Inside Package `lombok`](#1-you-can-safely-use-lombok-annotations-inside-package-lombok)
  * [2. You Should ***STAY AWAY*** From Using Lombok Annotations Inside Package `lombok.experimental`](#2-you-should-stay-away-from-using-lombok-annotations-inside-package-lombokexperimental)
  * [3. Special mention: ***DO NOT***. ***EVER***. ***USE***. `@lombok.experimental.UtilityClass`](#3-special-mention-do-not-ever-use-lombokexperimentalutilityclass)
  * [4. Edge Case: Use `@Accessors` with Great Care](#4-edge-case-use-accessors-with-great-care)
<!-- TOC -->

This guide serves as a foundational reference for engineers aiming to leverage the Lombok library effectively in their
projects. By following the best practices outlined here, you can significantly reduce boilerplate code and enhance code
readability and maintainability. All engineers are encouraged to adhere to these guidelines to ensure consistent and
efficient use of Lombok across projects.

## 1. You Can Safely Use Lombok Annotations Inside Package `lombok`

Some of them that we recommend to use are:

* `@Getter`

---

* `@Setter`

---

* `@Data` (see [this article](0005-normal-dto.MD) for more information)

---

* `@Value` (not to be confused with Spring's own `@org.springframework.beans.factory.annotation.Value`)

---

* `@NoArgsConstructor`

---

* `@AllArgsConstructor`

---

* `@RequiredArgsConstructor` (see [this article](0008-required-args-constructors.MD) for more information)

---

* `@Builder`

---

* `@With`

---

* `@ToString` and `@EqualsAndHashCode`

---

* `@SneakyThrows` (see [this article](0007-sneaky-throws.MD) for more information)

---

* `@Locked` (see [this article](0009-locked.MD) for more information), instead of `@Synchronized`

---

* `@Slf4j`

---

* and so on...

## 2. You Should ***STAY AWAY*** From Using Lombok Annotations Inside Package `lombok.experimental`

Experimental Lombok annotations are considered unstable, insufficiently tested, and may not handle edge cases
effectively (as noted on the official Lombok homepage). Unless there is a compelling reason to use them, it is strongly
recommended to avoid these annotations in order to maintain code reliability and stability.

## 3. Special mention: ***DO NOT***. ***EVER***. ***USE***. `@lombok.experimental.UtilityClass`

Quote from Lombok homepage:

> Due to limitations in `javac`, currently non-star static imports cannot be used to import stuff from
> `@UtilityClasses`; don't static import, or use star imports.

> If you are curious, you can see the status of this Lombok's annotation here:
>
> https://projectlombok.org/features/experimental/UtilityClass
>
> TL;DR: `@UtilityClass` is unlikely to go out from experimental status anytime soon, unless a drastic change is made
> to `javac`.

In short, the reckless usage of `@UtilityClass` will break your entire code base, literally, if you are not careful.

If you really want to create your own utility class, use this method instead:

```java
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YourUtils {
    // Your utility methods here
} 
```

However, if you want to ensure that even the Reflection API cannot circumvent the private constructor, something that
Lombok's `@NoArgsConstructor` currently doesn't provide, the recommended approach for creating utility classes is as
follows:

```java
public final class YourUtils {

    // You are not supposed to create new instance of a utility class
    private YourUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
```

## 4. Edge Case: Use `@Accessors` with Great Care

`@lombok.experimental.Accessors` is a potential experimental annotation that can be useful for building fluent APIs
within your code. For the time being, this is the only recommended usage of `@Accessors`:

```java
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class Employee {

  private UUID id;
  private String name;
  private int age;
}
```

The usage of `chain = true` will make your setter methods chainable (every invocation will return `this`), so you can
use the setters with fluent style like this:

```java

@Transactional
public void updateUser(UUID uuid) {
  Employee employee = getAdminUser(userId)
          .setName("Jan Žižka")
          .setAge(63);

  employeeRepository.save(employee);
}
```

It is recommended to use `@Accessors(chain = true)` in your entity or DTO classes if possible to extend the usage of
object accessors.

You can also use `@Accessors(fluent = true)`, which converts standard JavaBean-style getters like `getX()` into a more
fluent style like `x()`, similar to how you access fields in Java Records. However, be aware that enabling fluent
accessors may break existing functionality in your project, so use this option only if you're confident about how your
code will be working afterward.

```java
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
public class Employee {

  private UUID id;
  private String name;
  private int age;
}

Employee employee = new Employee(UUID.randomUUID(), "Houfnice", 10);

// employee.age() returns 10
// employee.name() returns "Houfnice"
```