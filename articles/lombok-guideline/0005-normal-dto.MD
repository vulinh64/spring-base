# Normal DTOs

## Table of Contents

<!-- TOC -->
* [Normal DTOs](#normal-dtos)
  * [Table of Contents](#table-of-contents)
  * [Powering up your DTOs](#powering-up-your-dtos)
    * [Example](#example)
  * [Special note about `@EqualsAndHashCode`](#special-note-about-equalsandhashcode)
    * [Example](#example-1)
<!-- TOC -->

## Powering up your DTOs

Normally, you can "decorate" your DTOs with the following Lombok annotations:

* `@Data` - This is the core annotation for any core DTO class

Those annotations below are optional:

* `@NoArgsConstructor` - If you want a no-args constructor.

---

* `@AllArgsConstructor` - If you want an all-args constructor for `@Builder` and `@RequiredArgsConstructor` cannot help
  you create such a constructor.

---

* `@Builder` - Builder creational pattern.

---

* `@With` - Rarely used, but you can use "withers" to create transitional temporary objects, for example,
  during Stream::map invocation.

---

* `@Accessors(chain = true)` - For "chaining" setters

### Example

```java
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Accessors(chain = true)
public class UserDTO {

  private UUID id;

  private String username;
  private LocalDateTime createdDateTime;

  // other fields and methods omitted for brevity
}
```

By using `@Data`, you implicitly include those annotations in your DTO classes: `@Getter`, `@Setter` (for non-final
fields), `@ToString`, `@EqualsAndHashCode` (for all fields), and `@RequiredArgsConstructor` (for all final fields).

## Special note about `@EqualsAndHashCode`

If you explicitly include `@EqualsAndHashCode`, it meant that you want to check the DTO's equality by your custom
criteria. It said case, you would use the annotation like this:

```java

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class YourDTO {
}
```

And annotate a field you chose to be the object's "representative" when comparing equality with:

```java

@EqualsAndHashCode.Include
private String equalityField;
```

### Example

```java
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Department {

  @EqualsAndHashCode.Include
  private UUID id;

  private String name;

  // other fields and methods omitted for brevity
}
```