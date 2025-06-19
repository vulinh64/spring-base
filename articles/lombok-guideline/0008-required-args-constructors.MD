# `@RequiredArgsConstructor` Annotation

## Table of Contents

<!-- TOC -->
* [`@RequiredArgsConstructor` Annotation](#requiredargsconstructor-annotation)
  * [Table of Contents](#table-of-contents)
  * [Foreword](#foreword)
  * [Usage](#usage)
  * [⚠️ Warning](#-warning)
  * [Copying Annotations](#copying-annotations)
<!-- TOC -->

## Foreword

The `@RequiredArgsConstructor` annotation deserves special mention: it helps eliminate boilerplate by automatically
generating a constructor for all final fields, making it especially useful in Spring Bean classes such as those
annotated with `@Component`, `@Service`, `@Controller`, or `@Configuration`.

In Spring Boot applications, and more broadly, in Spring Framework, it is generally recommended to use constructor
injection as the preferred form of dependency injection. This approach offers several advantages:

* It promotes immutability (no more accidental reassignment of dependency inside methods), which in turn enhances thread
  safety;

---

* It significantly improves testability, allowing for easier unit testing without relying on the Spring context;

---

* And it makes a class’s dependencies explicit, serving as a natural indicator when a service becomes too bloated: a
  signal that it may be violating the Single Responsibility Principle in the SOLID design principles.

## Usage

Generally, the usage of `@RequiredArgsConstructor` looks like this:

```java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  // If the number of dependencies exceeds a threshold (usually 7 per Sonar),
  // it's a sign to refactor the class to better follow the 
  // Single Responsibility Principle.    
  private final UserRepository userRepository;
  private final UserRoleService userRoleService;
  private final PasswordEncoder passwordEncoder;

  // Methods inside
}

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  // Methods inside
}

@Service
public class UserRoleService {
  // Methods inside
}
```

## ⚠️ Warning

This approach becomes less effective, even problematic, when your service class extends a base class with a
predefined constructor. In such cases, Lombok's `@RequiredArgsConstructor` may not work as intended, and attempting to
force it can lead to awkward workarounds that undermine the simplicity and elegance Lombok is meant to provide. As a
result, you’ll need to manually define your own all-arguments constructor to ensure proper initialization. Obviously,
implementing interfaces is fine.

## Copying Annotations

One common warning when attempting to use `@RequiredArgsConstructor` is:

```
Lombok does not copy the annotation [[annotation]] into the constructor
```

This issue commonly arises when a field is annotated with Spring’s `@Qualifier`, and occasionally with `@Value` as well.
By default, Lombok does not copy these annotations into the auto-generated constructor, which can lead to unexpected
behavior during dependency injection.

* (RECOMMENDED) Switch to using pure Java all-args constructor. Of course, this defeats the purpose of using Lombok, but
  it is the easiest way without requiring any tinkering to the project configurations.

---

* Configure Lombok: Create `lombok.config` file inside your `src` folder and add this line:

```lombok.config
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier
```

---

* Consider refactoring your code. Frequent usage of `@Qualifier` can indicate a need for more structured bean
  configuration. You might achieve better maintainability by using `@Profile` annotations for environment-specific beans
  or designating a primary bean with `@Primary` when multiple candidates of the same type exist.