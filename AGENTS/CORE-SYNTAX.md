# Core Java Syntax Guidelines

## Table of Contents

<!-- TOC -->
* [Core Java Syntax Guidelines](#core-java-syntax-guidelines)
  * [Table of Contents](#table-of-contents)
  * [Type Inference](#type-inference)
  * [Text Blocks](#text-blocks)
  * [String Formatting](#string-formatting)
  * [Local Variable Extraction for Repeated Method Calls](#local-variable-extraction-for-repeated-method-calls)
  * [Code Spacing and Indentation](#code-spacing-and-indentation)
  * [Ternary Operator for Simple Conditions](#ternary-operator-for-simple-conditions)
<!-- TOC -->

## Type Inference

**Prefer `var` over explicit data types** when the type is obvious from the context.

```java
// Preferred
var name = "John Doe";
var users = new ArrayList<User>();
var config = ConfigLoader.load();

// Avoid
String name = "John Doe";
List<User> users = new ArrayList<>();
ArrayList<User> users = new ArrayList<>();
Config config = ConfigLoader.load();
```

## Text Blocks

**Prefer text blocks** when a string contains two or more escaped characters like `\n`, `\t`, `\"`, etc.

```java
// Preferred - using text block
// Pay attention to the ending backslash to avoid unwanted new line
// Unless it is not important (not using for comparison)
var json = """
    {
      "name": "John",
      "age": 30,
      "city": "New York"
    }\
    """;

var html = """
    <html>
      <body>
        <h1>Welcome</h1>
      </body>
    </html>\
    """;

// Avoid - multiple escape characters
var json = "{\n  \"name\": \"John\",\n  \"age\": 30,\n  \"city\": \"New York\"\n}";
var html = "<html>\n  <body>\n    <h1>Welcome</h1>\n  </body>\n</html>";
```

## String Formatting

**Prefer instance method `formatted()`** over static method `String.format()`.

```java
// Preferred
var message = "Hello, %s! You have %d messages.".formatted(userName, messageCount);
var url = "https://api.example.com/users/%s/posts/%d".formatted(userId, postId);

// Avoid
var message = String.format("Hello, %s! You have %d messages.", userName, messageCount);
var url = String.format("https://api.example.com/users/%s/posts/%d", userId, postId);
```

Benefits:

- More readable left-to-right flow

- Easier to chain with other string operations

- Consistent with modern Java API design

## Local Variable Extraction for Repeated Method Calls

**Prefer extracting to a local variable** when a method (especially getters) is called multiple times.

```java
// Preferred
var userName = user.getName();
var greeting = "Hello, " + userName;
var logMessage = "User logged in: " + userName;
log.info(logMessage);

// Avoid
var greeting = "Hello, " + user.getName();
var logMessage = "User logged in: " + user.getName();
log.info(logMessage);
```

**Exception**: Methods that return different values on each call should NOT be extracted:

```java
// Correct - don't extract, these return different values each time
var startTime = Instant.now();
performOperation();
var endTime = Instant.now();

var id1 = UUID.randomUUID();
var id2 = UUID.randomUUID();

var random = new Random();
var roll1 = random.nextInt(6);
var roll2 = random.nextInt(6);
```

**Side effect warning**: If a method call might trigger side effects, add a comment:

```java
var result = service.processAndLog(data);  // Beware of side effect
var message = buildMessage(result);
var notification = sendNotification(message);  // Beware of side effect
```

Example: every temporal class method calls (like `Instant.now()`, `LocalDateTime.now()`, etc...) should NOT be extracted unless stated otherwise. Leave the users to decide based on their use case.

## Code Spacing and Indentation

**Prefer two-space gap** from definition to the first line of code. For subsequent lines in the same block (like long method chains), use four-space indentation to align with Google Java Format rules.

```java
// Preferred - 2 spaces after definition
public void processUser(User user) {
  var name = user.getName();
  var email = user.getEmail();
  
  // Long method chain - first line 2 spaces, continuation 4 spaces
  var result = someService
      .withConfiguration(config)
      .andSettings(settings)
      .process(user);
  
  logger.info("Processed user: {}", name);
}

// Another example with builder pattern
public User createUser() {
  // Immediately return the built object
  return User.builder()
      .withName("John Doe")
      .withEmail("john@example.com")
      .withRole(Role.ADMIN)
      .build();
}
```

```java
// Class-level spacing
public class UserService {
  
  private final UserRepository repository;
  private final Logger logger;
  
  public UserService(UserRepository repository) {
    this.repository = repository;
    this.logger = LoggerFactory.getLogger(UserService.class);
  }
  
  public User findUser(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }
}
```

## Ternary Operator for Simple Conditions

**Prefer ternary operator for simple if conditions when possible.**

```java
// Preferred - ternary operator for simple conditions
var status = user.isActive() ? "Active" : "Inactive";

var discount = isPremiumMember ? 0.20 : 0.10;

var greeting = timeOfDay < 12 ? "Good morning" : "Good afternoon";

var displayName = user.getName() != null ? user.getName() : "Anonymous";

// Method return
public String getAccessLevel(User user) {
  return user.isAdmin() ? "Full Access" : "Limited Access";
}

public int getShippingCost(boolean isPremium) {
  return isPremium ? 0 : 10;
}
```

```java
// Avoid - verbose if-else for simple conditions
String status;

if (user.isActive()) {
  status = "Active";
} else {
  status = "Inactive";
}

double discount;

if (isPremiumMember) {
  discount = 0.20;
} else {
  discount = 0.10;
}
```

When NOT to use ternary:

- Complex conditions with multiple statements

- Nested ternary operators (hard to read)

- When the condition or values are lengthy

```java
// Avoid - nested ternary (hard to read)
var price = isPremium ? (isHoliday ? 80 : 100) : (isHoliday ? 90 : 120);

// Preferred - use if-else for clarity
int price;
if (isPremium) {
  price = isHoliday ? 80 : 100;
} else {
  price = isHoliday ? 90 : 120;
}

// Avoid - ternary with side effects
var result = condition ? saveToDatabase(data) : deleteFromDatabase(data);

// Preferred - use if-else for side effects
if (condition) {
  saveToDatabase(data);
} else {
  deleteFromDatabase(data);
}
```

Benefits of ternary operator:

- More concise for simple conditions

- Enforces single assignment (immutability)

- Reads left-to-right naturally

- Reduces boilerplate
