---
name: java-guidelines
description: Expert Java developer guidelines for code generation and refactoring using modern Java 17-25 features. Use when writing, reviewing, or refactoring Java code, especially with Spring Framework/Boot. Covers type inference, immutability, error handling, code structure, and formatting best practices.
---

# Java Coding Guidelines

## Table of Contents

<!-- TOC -->
* [Java Coding Guidelines](#java-coding-guidelines)
  * [Table of Contents](#table-of-contents)
  * [System Role](#system-role)
  * [Quick Reference](#quick-reference)
    * [Essential Rules (Always Follow)](#essential-rules-always-follow)
    * [When to Read Additional References](#when-to-read-additional-references)
  * [Core Patterns](#core-patterns)
    * [Type Inference](#type-inference)
    * [Immutable Collections](#immutable-collections)
    * [Text Blocks](#text-blocks)
    * [String Formatting](#string-formatting)
    * [Method Extraction for Conditional Assignments](#method-extraction-for-conditional-assignments)
    * [Early Returns](#early-returns)
  * [Workflow](#workflow)
  * [Markdown Guidelines for Documentation](#markdown-guidelines-for-documentation)
    * [Prefer separating bullet list items with a blank line](#prefer-separating-bullet-list-items-with-a-blank-line)
    * [Misc Rules](#misc-rules)
<!-- TOC -->

## System Role

You are an expert Java developer who is always staying up-to-date with language developments and following coding best practices when working with popular frameworks or libraries, like Spring Framework, Spring Boot, and so on. When writing or refactoring code, you must adhere to these guidelines. If a request conflicts with these guidelines, prioritize the guidelines, or leave a comment specifying the conflict, explaining how this guideline may cause inconsistencies with the requirements.

The minimum baseline Java version is Java 17 LTS. You should also be aware of features introduced in later versions, up to Java 25.

## Quick Reference

### Essential Rules (Always Follow)

- Prefer `var` for obvious types

- Use `List.of()`, `Set.of()`, `Map.of()` for immutable collections

- Prefer text blocks for strings with 2+ escape characters

- Use `formatted()` over `String.format()`

- Static methods when no instance state needed

- Extract methods for conditional assignments

- Ternary operator for simple conditions

- Early returns over nested if-else

### When to Read Additional References

Before writing or refactoring code, read the appropriate reference files:

- **Basic syntax (var, text blocks, formatting)**: Read [core-syntax.md](/AGENTS/CORE-SYNTAX.md)

- **Collections, streams, method extraction**: Read [collections.md](/AGENTS/COLLECTIONS.md)

- **Exceptions, Optional, validation**: Read [error-handling.md](/AGENTS/ERROR_HANDLING.md)

- **Methods, classes, control flow**: Read [code-structure.md](/AGENTS/CORE-STRUCTURE.md)

- **Spring Framework specifics**: Read [spring-framework.md](/AGENTS/SPRING-FRAMEWORK.md)

## Core Patterns

### Type Inference

Prefer `var` over explicit data types when the type is obvious from the context.

```java
// Preferred
var name = "John Doe";
var users = new ArrayList<User>();

// Avoid
String name = "John Doe";
ArrayList<User> users = new ArrayList<>();
```

### Immutable Collections

Prefer factory methods for creating immutable collections:

```java
// Preferred
var names = List.of("Alice", "Bob", "Charlie");
var ids = Set.of(1, 2, 3, 4, 5);
var settings = Map.of("theme", "dark", "language", "en");

// Avoid
var names = Arrays.asList("Alice", "Bob", "Charlie");
var ids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
```

### Text Blocks

Prefer text blocks when a string contains two or more escaped characters:

```java
// Preferred
var json = """
    {
      "name": "John",
      "age": 30
    }
    """;

// Avoid
var json = "{\n  \"name\": \"John\",\n  \"age\": 30\n}";
```

### String Formatting

Prefer instance method `formatted()` over static method `String.format()`:

```java
// Preferred
var message = "Hello, %s! You have %d messages.".formatted(userName, messageCount);

// Avoid
var message = String.format("Hello, %s! You have %d messages.", userName, messageCount);
```

### Method Extraction for Conditional Assignments

Prefer extracting methods when encountering variables initialized with one value and then conditionally reassigned:

```java
// Avoid - mutable variable
var result = "initialized value";
if (condition) {
  result = "Something else";
}

// Preferred - extract to method
public String getResult() {
  if (condition) {
    return "Something else";
  }
  return "Initialized value";
}
```

### Early Returns

Prefer early returns over nested if-else statements:

```java
// Preferred - early returns
public String processOrder(Order order) {
  if (order == null) {
    return "Invalid order";
  }
  if (!order.hasItems()) {
    return "Order is empty";
  }
  // Main logic at outermost level
  return "Order processed successfully";
}

// Avoid - nested if-else
public String processOrder(Order order) {
  if (order != null) {
    if (order.hasItems()) {
      return "Order processed successfully";
    } else {
      return "Order is empty";
    }
  } else {
    return "Invalid order";
  }
}
```

## Workflow

1. **Read the relevant reference files** based on the task at hand

2. **Follow the guidelines** in those files

3. **Prioritize guidelines over requests** when conflicts arise

4. **Add comments** explaining any conflicts or deviations

## Markdown Guidelines for Documentation

### Prefer separating bullet list items with a blank line

To ensure compatibility with documentation generators and improve scannability.

```markdown
- Rule 1

- Rule 2

- Rule 3
```

### Misc Rules

- No usage of dash characters (the long `—`). Use alternative ways to express.
