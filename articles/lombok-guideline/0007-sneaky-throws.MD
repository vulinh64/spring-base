# `@SneakyThrows` Annotation

## Table of Contents

<!-- TOC -->
* [`@SneakyThrows` Annotation](#sneakythrows-annotation)
  * [Table of Contents](#table-of-contents)
  * [Forewords](#forewords)
  * [Usage](#usage)
    * [Example](#example)
<!-- TOC -->

## Forewords

Using Lombok’s `@SneakyThrows` annotation is generally seen as a violation of Java’s exception handling contract. While
it can be practical in real-world scenarios, such as when dealing with checked exceptions that are unlikely to occur
unless a serious issue arises. It should be used with great caution.

Overusing it can obscure the flow of exceptions and hinder code readability and maintainability.

Be pragmatic in your approach, and carefully weigh the benefits against the potential trade-offs before applying it.

## Usage

The `@SneakyThrows` annotation allows you to bypass handling checked exceptions explicitly, effectively letting the flow
be interrupted when necessary. This can be especially useful in scenarios like API calls, where a "fail-fast" approach
is preferred over a "fail-safe" one, for instance, encountering a malformed JSON payload is not something your
application can meaningfully recover from (and your application shouldn't have to recover from such a case), so 
gracefully continuing execution wouldn't make sense.

### Example

Instead of manually wrapping your checked exceptions in unchecked exception `throw` statement like below:

```java
import java.io.IOException;

public void doSomething() throws IOException {
  // Do something here
}

public void execute() {
  try {
    doSomething();
  } catch (Exception e) {
    throw new RuntimeException(e);
  }
}
```

You can opt for using `@SneakyThrows` annotation like this:

```java
import lombok.SneakyThrows;

import java.io.IOException;

public void doSomething() throws IOException {
  // Do something here
}

@SneakyThrows
public void execute() {
  doSomething();
}
```

It is still advisable to wrap checked exceptions in custom unchecked exceptions. This approach ensures that your
application's global exception handling, typically defined in a `@ControllerAdvice`-annotated class using
`@ExceptionHandler` methods, can catch and process them consistently and effectively. Only use `@SneakyThrows`
where you know that handling a checked exception makes little sense.