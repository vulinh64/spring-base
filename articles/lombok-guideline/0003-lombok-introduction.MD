# Lombok Introduction

---

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
* `@Setter`
* `@Data`
* `@Value` (not to be confused with Spring's own `@org.springframework.beans.factory.annotation.Value`)
* `@NoArgsConstructor`
* `@AllArgsConstructor`
* `@RequiredArgsConstructor` (this annotation will have its own dedicated article for its usages)
* `@Builder`
* `@With`
* `@ToString` and `@EqualsAndHashCode`
* `@SneakyThrows`
* `@Locked`
* `@Slf4j`
* and so on...

The usage of those annotations will be specified in dedicated articles.

## 2. You Should ***STAY AWAY*** From Using Lombok Annotations Inside Package `lombok.experimental`

Experimental Lombok annotations are considered unstable, insufficiently tested, and may not handle edge cases
effectively (as noted on the official Lombok homepage). Unless there is a compelling reason to use them, it is strongly
recommended to avoid these annotations in order to maintain code reliability and stability.

## 3. Special mention: ***DO NOT***. ***EVER***. ***USE***. `@lombok.experimental.UtilityClass`

Quote from Lombok homepage:

> Due to limitations in `javac`, currently non-star static imports cannot be used to import stuff from
> `@UtilityClasses`; don't static import, or use star imports.

You can see the status of this Lombok's annotation here:

> https://projectlombok.org/features/experimental/UtilityClass

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

Nevertheless, the recommended approach when creating utility classes is as following, if you want to ensure that even
Reflection API cannot circumvent the private constructor (something that Lombok `@NoArgsConstructor` cannot provide for
the time being):

```java
public final class YourUtils {

    // You are not supposed to create new instance of a utility class
    private YourUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
```

## 4. Edge Case: Use `@Accessors` with Great Care

`@lombok.experimental.Accessors` is a promising experimental annotation that can be useful for building fluent APIs
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