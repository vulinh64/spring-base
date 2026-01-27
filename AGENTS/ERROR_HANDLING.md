# Error Handling and Validation Guidelines

## Table of Contents

<!-- TOC -->
* [Error Handling and Validation Guidelines](#error-handling-and-validation-guidelines)
  * [Table of Contents](#table-of-contents)
  * [Optional Return Type](#optional-return-type)
  * [Empty Collections Over Null](#empty-collections-over-null)
  * [Exception Handling](#exception-handling)
    * [Exception Constructors](#exception-constructors)
    * [Rethrowing Exceptions](#rethrowing-exceptions)
  * [Nested Null Checks](#nested-null-checks)
    * [Implementation Requirements](#implementation-requirements)
  * [Early Returns](#early-returns)
    * [Warning](#warning)
    * [Benefits of early returns:](#benefits-of-early-returns)
<!-- TOC -->

## Optional Return Type

**Prefer `Optional`** as a return type instead of returning `null` to force callers to handle the absence of a value.

```java
// Preferred - using Optional
public Optional<User> findUserById(String id) {
  return userRepository.findById(id);
}

public Optional<String> getMiddleName(User user) {
  return Optional.ofNullable(user.getMiddleName());
}

public Optional<Order> findLatestOrder(String customerId) {
  var orders = orderRepository.findByCustomerId(customerId);
  return orders.isEmpty() 
      ? Optional.empty() 
      : Optional.of(orders.get(0));
}

// Usage - forces explicit handling
var user = userService.findUserById("123")
    .orElseThrow(() -> new UserNotFoundException("123"));

var displayName = userService.findUserById("123")
    .map(User::getName)
    .orElse("Guest");

userService.findUserById("123")
    .ifPresent(user -> emailService.sendWelcomeEmail(user));
```

```java
// Avoid - returning null
public User findUserById(String id) {
  return userRepository.findById(id).orElse(null);
}

public String getMiddleName(User user) {
  return user.getMiddleName();  // Could be null
}

// Usage - easy to forget null check
var user = userService.findUserById("123");
if (user != null) {  // Easy to forget this check!
  user.getName();
}
```

Benefits of Optional:

- Makes the absence of a value explicit in the API

- Forces callers to handle the "no value" case

- Provides functional-style methods (`map`, `flatMap`, `filter`, `orElse`, etc.)

- Reduces `NullPointerException` risks

- Self-documenting code

**When NOT to use Optional:**

- For fields in domain objects (use null instead)

- As method parameters (use overloading or null checks instead)

- In collections (use empty collections instead)

## Empty Collections Over Null

**Prefer returning empty collections** instead of null if the return type is a Collection unless explicitly stated otherwise.

```java
// Preferred - return empty collection
public List<Order> findOrdersByCustomerId(String customerId) {
  var orders = orderRepository.findByCustomerId(customerId);
  return orders != null ? orders : Collections.emptyList();
}

public Set<String> getUserPermissions(String userId) {
  var user = userRepository.findById(userId);
  return user != null ? user.getPermissions() : Collections.emptySet();
}

public Map<String, String> getUserMetadata(String userId) {
  var user = userRepository.findById(userId);
  return user != null ? user.getMetadata() : Collections.emptyMap();
}

// Even simpler with modern Java
public List<Order> findOrdersByCustomerId(String customerId) {
  return orderRepository.findByCustomerId(customerId)
      .orElse(List.of());
}
```

```java
// Avoid - returning null for collections
public List<Order> findOrdersByCustomerId(String customerId) {
  var orders = orderRepository.findByCustomerId(customerId);
  return orders;  // Could be null!
}

// Usage - requires null check
var orders = orderService.findOrdersByCustomerId("123");
if (orders != null) {  // Annoying null check
  for (var order : orders) {
    // process order
  }
}
```

```java
// Preferred usage - no null check needed
var orders = orderService.findOrdersByCustomerId("123");
for (var order : orders) {  // Works even if empty
  // process order
}

var orderCount = orderService.findOrdersByCustomerId("123").size();  // Safe
```

Benefits:

- Eliminates `NullPointerException` for collection operations

- Simplifies caller code (no null checks needed)

- More consistent API behavior

- Empty collections are lightweight (often singletons)

## Exception Handling

### Exception Constructors

**Always create an exception constructor** that takes a `Throwable` parameter to support exception chaining.

```java
// Preferred - complete exception class
public class UserNotFoundException extends RuntimeException {
  
  public UserNotFoundException(String message) {
    super(message);
  }
  
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public UserNotFoundException(Throwable cause) {
    super(cause);
  }
}

// Another example with custom fields
public class ValidationException extends RuntimeException {
  
  private final List<String> validationErrors;
  
  public ValidationException(String message, List<String> validationErrors) {
    super(message);
    this.validationErrors = List.copyOf(validationErrors);
  }
  
  public ValidationException(String message, List<String> validationErrors, Throwable cause) {
    super(message, cause);
    this.validationErrors = List.copyOf(validationErrors);
  }
  
  public List<String> getValidationErrors() {
    return validationErrors;
  }
}
```

```java
// Avoid - missing Throwable constructor
public class UserNotFoundException extends RuntimeException {
  
  public UserNotFoundException(String message) {
    super(message);
  }
  
  // Missing: constructor with Throwable parameter
}
```

### Rethrowing Exceptions

**When rethrowing an exception, always include the original exception** to preserve the full stack trace.

```java
// Preferred - preserving original exception
public User loadUser(String id) {
  try {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
  } catch (DatabaseException e) {
    throw new UserServiceException("Failed to load user: " + id, e);  // Original exception included
  }
}

public void processOrder(Order order) {
  try {
    paymentService.processPayment(order);
  } catch (PaymentException e) {
    var message = "Payment processing failed for order: " + order.getId();
    throw new OrderProcessingException(message, e);  // Chain the exception
  }
}

// Converting checked to unchecked
public String readFile(String path) {
  try {
    return Files.readString(Path.of(path));
  } catch (IOException e) {
    throw new FileReadException("Failed to read file: " + path, e);  // Wrap and chain
  }
}
```

```java
// Avoid - losing original exception
public User loadUser(String id) {
  try {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
  } catch (DatabaseException e) {
    throw new UserServiceException("Failed to load user: " + id);  // Original exception lost!
  }
}

// Avoid - swallowing exception details
public void processOrder(Order order) {
  try {
    paymentService.processPayment(order);
  } catch (PaymentException e) {
    throw new OrderProcessingException("Payment failed");  // No context, no cause
  }
}
```

Benefits of exception chaining:

- Preserves complete stack trace for debugging

- Maintains context about the root cause

- Helps diagnose issues in production

- Follows Java best practices

- Essential for troubleshooting complex systems

Example of preserved stack trace:

```
OrderProcessingException: Payment processing failed for order: 12345
    at OrderService.processOrder(OrderService.java:45)
    Caused by: PaymentException: Insufficient funds
        at PaymentService.processPayment(PaymentService.java:78)
        Caused by: BankAPIException: Account balance too low
            at BankAPIClient.debit(BankAPIClient.java:123)
```

## Nested Null Checks

**Prefer Optional chaining** when nested null checks or conditional logic exceed three levels to improve code clarity and functional flow.

```java
// Preferred - clear functional chain
Optional.ofNullable(user)
    .map(User::getName)
    .filter(name -> name.toUpperCase().startsWith("A"))
    .ifPresent(this::processUser);

// Avoid - verbose and error-prone nested checks
if (user != null
    && user.getName() != null
    && user.getName().toUpperCase().startsWith("A")) {
  processUser(user.getName());
}
```

### Implementation Requirements

- Before writing or refactoring code into an Optional chain, you must probe the definition of the involved classes (e.g., checking the User.java file) to confirm method signatures and return types.

- Use `map()` for object transformations and `filter()` for boolean conditions.

- Ensure the chain is completed with an appropriate terminal operation such as `ifPresent()`, `orElse()`, or `orElseThrow()`.

## Early Returns

**Prefer early returns over nested if-else statements** to reduce indentation and improve readability.

Early returns handle edge cases and invalid conditions first, allowing the main logic to remain at the outermost level without deep nesting. This approach makes code easier to read and understand by eliminating the need to track multiple levels of conditional blocks.

```java
// Preferred - early returns
public String processOrder(Order order) {
  if (order == null) {
    return "Invalid order";
  }

  if (!order.hasItems()) {
    return "Order is empty";
  }

  if (!order.getCustomer().isActive()) {
    return "Customer account inactive";
  }

  // Main logic at outermost level
  var result = paymentService.process(order);
  
  emailService.sendConfirmation(order);
  
  return "Order processed successfully";
}
```

```java
// Avoid - nested if-else
public String processOrder(Order order) {
  if (order != null) {
    if (order.hasItems()) {
      if (order.getCustomer().isActive()) {
          // Main logic deeply nested
          var result = paymentService.process(order);
          
          emailService.sendConfirmation(order);
          
          return "Order processed successfully";
      } else {
        return "Customer account inactive";
      }
    } else {
     return "Order is empty";
    }
  } else {
    return "Invalid order";
  }
}
```

### Warning

If a method contains more than 5 return statements, add a comment indicating potential complexity:

```java
// WARNING: Method has 6 return statements - consider refactoring to reduce complexity
public String validateUser(User user) {
  if (user == null) {
    return "User is null";
  }

  if (user.getName() == null) {
    return "Name is required";
  }

  if (user.getEmail() == null) {
    return "Email is required";
  }

  if (!user.getEmail().contains("@")) {
    return "Invalid email format";
  }

  if (user.getAge() < 18) {
    return "User must be 18 or older";
  }

  if (!user.hasAcceptedTerms()) {
    return "Terms must be accepted";
  }

  return "Valid";
}
```

### Benefits of early returns:

- Reduces nesting and indentation levels

- Makes edge cases and validations explicit and obvious

- Improves code readability by keeping main logic at the top level

- Easier to add or remove validation conditions

- Follows the "fail fast" principle
