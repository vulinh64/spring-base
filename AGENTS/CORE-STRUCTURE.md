# Code Structure and Organization Guidelines

## Table of Contents

<!-- TOC -->
* [Code Structure and Organization Guidelines](#code-structure-and-organization-guidelines)
  * [Table of Contents](#table-of-contents)
  * [Direct Return Statements](#direct-return-statements)
  * [Switch Expressions](#switch-expressions)
  * [Descriptive Boolean Names](#descriptive-boolean-names)
  * [Method Extraction for Conditional Assignments](#method-extraction-for-conditional-assignments)
  * [Static Methods](#static-methods)
<!-- TOC -->

## Direct Return Statements

**Always return the result immediately** without assigning to a variable if there are no operations on that variable prior to the return statement.

```java
// Preferred - direct return
public String getUserName(User user) {
  return user.getName();
}

public List<String> getActiveUserIds(List<User> users) {
  return users.stream()
      .filter(User::isActive)
      .map(User::getId)
      .toList();
}

public int calculateTotal(int price, int quantity) {
  return price * quantity;
}

public User findUserById(String id) {
  return userRepository.findById(id)
      .orElseThrow(() -> new UserNotFoundException(id));
}
```

```java
// Avoid - unnecessary variable assignment
public String getUserName(User user) {
  var name = user.getName();
  return name;
}

public List<String> getActiveUserIds(List<User> users) {
  var activeIds = users.stream()
      .filter(User::isActive)
      .map(User::getId)
      .toList();
  
  return activeIds;
}

public int calculateTotal(int price, int quantity) {
  var total = price * quantity;
  
  return total;
}
```

**When to use a variable before return:**

- When you need to perform operations on the result

- When you need to log or debug the value

- When the variable improves readability for complex expressions

```java
// Acceptable - variable is used before return
public User createUser(UserRequest request) {
  var user = userRepository.save(request.toEntity());
  auditLogger.log("Created user: " + user.getId());
  emailService.sendWelcomeEmail(user);
  return user;
}

// Acceptable - improves readability for complex logic
public boolean isEligibleForDiscount(Order order) {
  var hasLoyaltyMembership = order.getCustomer().hasLoyaltyMembership();
  var meetsMinimumAmount = order.getTotal() >= MINIMUM_DISCOUNT_AMOUNT;
  var isFirstTimeDiscount = !order.getCustomer().hasUsedDiscount();
  
  return hasLoyaltyMembership && (meetsMinimumAmount || isFirstTimeDiscount);
}
```

## Switch Expressions

**Prefer switch expressions** over traditional switch statements to ensure all cases are covered and to return values directly.

```java
// Preferred - switch expression
public String getStatusMessage(OrderStatus status) {
  return switch (status) {
    case PENDING -> "Your order is being processed";
    case CONFIRMED -> "Your order has been confirmed";
    case SHIPPED -> "Your order is on the way";
    case DELIVERED -> "Your order has been delivered";
    case CANCELLED -> "Your order was cancelled";
  };
}

public int getDiscountPercentage(MembershipLevel level) {
  return switch (level) {
    case BRONZE -> 5;
    case SILVER -> 10;
    case GOLD -> 15;
    case PLATINUM -> 20;
  };
}

// Multi-line logic per case
public PaymentProcessor getPaymentProcessor(PaymentMethod method) {
  return switch (method) {
    case CREDIT_CARD -> {
      var processor = new CreditCardProcessor();
      processor.setSecurityLevel(SecurityLevel.HIGH);
      yield processor;
    }
    case PAYPAL -> {
      var processor = new PayPalProcessor();
      processor.enableSandbox(false);
      yield processor;
    }
    case BANK_TRANSFER -> new BankTransferProcessor();
  };
}
```

```java
// Avoid - traditional switch statement
public String getStatusMessage(OrderStatus status) {
  String message;
  switch (status) {
    case PENDING:
      message = "Your order is being processed";
      break;
    case CONFIRMED:
      message = "Your order has been confirmed";
      break;
    case SHIPPED:
      message = "Your order is on the way";
      break;
    case DELIVERED:
      message = "Your order has been delivered";
      break;
    case CANCELLED:
      message = "Your order was cancelled";
      break;
    default:
      message = "Unknown status";
      break;
  }
  return message;
}
```

Benefits of switch expressions:

- Exhaustiveness checking (compiler ensures all cases are covered)

- No fall-through bugs

- Direct value return (no intermediate variables)

- More concise and readable

- Expression-based (can be used in assignments, returns, etc.)

## Descriptive Boolean Names

**Prefer descriptive boolean names** that sound like a question using prefixes like `is`, `has`, `can`, `should`.

```java
// Preferred - question-like boolean names
public class User {
  
  private boolean isActive;
  private boolean hasVerifiedEmail;
  private boolean canEditPosts;
  private boolean shouldReceiveNotifications;
  
  public boolean isActive() {
    return isActive;
  }
  
  public boolean hasVerifiedEmail() {
    return hasVerifiedEmail;
  }
  
  public boolean canEditPosts() {
    return canEditPosts;
  }
  
  public boolean shouldReceiveNotifications() {
    return shouldReceiveNotifications;
  }
}

// Method names
public boolean isEligibleForDiscount(User user) {
  return user.hasLoyaltyMembership() && user.getTotalPurchases() > 1000;
}

public boolean canAccessResource(User user, Resource resource) {
  return user.hasPermission(resource.getRequiredPermission());
}

public boolean shouldSendReminder(Order order) {
  return order.isPending() && order.getCreatedDate().isBefore(LocalDate.now().minusDays(3));
}
```

```java
// Avoid - unclear or non-question boolean names
public class User {
  
  private boolean active;  // Less clear
  private boolean verified;  // Verified what?
  private boolean edit;  // Verb, not a state
  private boolean notifications;  // Ambiguous
  
  public boolean getActive() {  // Sounds like getting something
    return active;
  }
}

public boolean eligibleForDiscount(User user) {  // Missing "is"
  return user.loyaltyMembership() && user.getTotalPurchases() > 1000;
}

public boolean accessResource(User user, Resource resource) {  // Sounds like an action
  return user.hasPermission(resource.getRequiredPermission());
}
```

Benefits:

- Reads naturally in conditionals: `if (user.isActive())`

- Self-documenting code

- Follows JavaBean naming conventions

- Clear intent and meaning

Common patterns:

- `is` prefix: state or condition (`isActive`, `isEmpty`, `isValid`)

- `has` prefix: possession or presence (`hasPermission`, `hasChildren`, `hasExpired`)

- `can` prefix: ability or capability (`canEdit`, `canDelete`, `canAccess`)

- `should` prefix: recommendation or requirement (`shouldRetry`, `shouldCache`, `shouldNotify`)

## Method Extraction for Conditional Assignments

**Prefer extracting methods** when encountering variables initialized with one value and then conditionally reassigned.

```java
// Avoid - mutable variable with conditional reassignment
var result = "initialized value";

if (condition) {
  result = "Something else";
}

// other code that can change result
```

**Preferred approach** - extract to a method:

```java
public String getResult() {
  if (condition) {
    return "Something else";
  }
  
  // other return statements based on other conditions
  
  return "Initialized value";
}
```

This approach:

- Eliminates mutable local variables

- Makes the logic more testable

- Improves code readability

- Prevents accidental reassignments

## Static Methods

**Prefer static methods** when the method does not use any instance variables or instance methods.

```java
// Preferred - static method
public class MathUtils {
  
  public static int add(int a, int b) {
    return a + b;
  }
  
  public static boolean isEven(int number) {
    return number % 2 == 0;
  }
  
  public static String formatCurrency(double amount) {
    return "$%.2f".formatted(amount);
  }
}

// Usage
var sum = MathUtils.add(5, 3);
var formatted = MathUtils.formatCurrency(19.99);
```

```java
// Instance method when state is needed
public class Calculator {
  
  private final double taxRate;
  
  public Calculator(double taxRate) {
    this.taxRate = taxRate;
  }
  
  // Instance method - uses taxRate field
  public double calculateTotal(double amount) {
    return amount * (1 + taxRate);
  }
  
  // Static method - doesn't use instance state
  public static double round(double value, int decimals) {
    var multiplier = Math.pow(10, decimals);
    return Math.round(value * multiplier) / multiplier;
  }
}
```

Benefits of static methods:

- Clear indication that the method is stateless

- Can be called without creating an instance

- Easier to test and reason about

- Better performance (no implicit `this` parameter)