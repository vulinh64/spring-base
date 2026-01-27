# Collections and Immutability Guidelines

## Table of Contents

<!-- TOC -->
* [Collections and Immutability Guidelines](#collections-and-immutability-guidelines)
  * [Table of Contents](#table-of-contents)
  * [Immutable Collections](#immutable-collections)
  * [Records for Data Carriers](#records-for-data-carriers)
  * [Defensive Copying for Mutable Collections](#defensive-copying-for-mutable-collections)
    * [Arrays](#arrays)
    * [Lists, Sets, and Maps](#lists-sets-and-maps)
  * [Stream Collection](#stream-collection)
<!-- TOC -->

## Immutable Collections

**Prefer factory methods** for creating immutable collections:

```java
// Preferred
var names = List.of("Alice", "Bob", "Charlie");
var ids = Set.of(1, 2, 3, 4, 5);
var settings = Map.of(
  "theme", "dark",
  "language", "en",
  "timeout", "30"
);

// For larger maps, use Map.ofEntries
var config = Map.ofEntries(
  Map.entry("key1", "value1"),
  Map.entry("key2", "value2"),
  Map.entry("key3", "value3")
);

// Avoid
var names = Arrays.asList("Alice", "Bob", "Charlie");
var ids = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
var settings = new HashMap<>();
settings.put("theme", "dark");
settings.put("language", "en");
```

## Records for Data Carriers

**Prefer Java records** for all data carrier classes (DTOs, value objects, etc.) unless stated otherwise.

```java
// Preferred - using record
public record User(String id, String name, String email, LocalDate birthDate) {
}

public record OrderRequest(
    String customerId,
    List<String> productIds,
    String shippingAddress,
    PaymentMethod paymentMethod
) {
}

public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    int statusCode
) {
}
```

```java
// Avoid - traditional class for simple data carriers
public class User {
  
  private final String id;
  private final String name;
  private final String email;
  private final LocalDate birthDate;
  
  public User(String id, String name, String email, LocalDate birthDate) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.birthDate = birthDate;
  }
  
  // getters, equals, hashCode, toString...
}
```

Benefits of records:

- Concise syntax

- Automatic implementation of `equals()`, `hashCode()`, `toString()`

- Immutable by default

- Clear intent as data carriers

**When NOT to use records:**

- Classes requiring mutable state

- Classes with complex business logic

- Classes requiring inheritance (records are final)

- JPA entities (use `@Entity` classes instead)

## Defensive Copying for Mutable Collections

**Always create defensive copies** when dealing with mutable collections to prevent external modification.

### Arrays

```java
// Preferred - defensive copy
public class DataHolder {
  
  private final int[] values;
  
  public DataHolder(int[] values) {
    this.values = Arrays.copyOf(values, values.length);
  }
  
  public int[] getValues() {
    return Arrays.copyOf(values, values.length);
  }
}

// Avoid - direct assignment allows external modification
public class DataHolder {
  
  private final int[] values;
  
  public DataHolder(int[] values) {
    this.values = values;  // Caller can modify the array!
  }
  
  public int[] getValues() {
    return values;  // Caller can modify internal state!
  }
}
```

### Lists, Sets, and Maps

```java
// Preferred - defensive copy with immutable collections
public record UserGroup(
    String name,
    List<String> memberIds,
    Set<String> permissions,
    Map<String, String> metadata
) {
  
  public UserGroup {
    memberIds = List.copyOf(memberIds);
    permissions = Set.copyOf(permissions);
    metadata = Map.copyOf(metadata);
  }
}

// Usage
var members = new ArrayList<String>();
members.add("user1");
members.add("user2");

var group = new UserGroup("admins", members, Set.of("READ", "WRITE"), Map.of());

// This won't affect the record's internal state
members.add("user3");
```

```java
// Traditional class example
public class Configuration {
  
  private final List<String> allowedHosts;
  private final Map<String, String> settings;
  
  public Configuration(List<String> allowedHosts, Map<String, String> settings) {
    this.allowedHosts = List.copyOf(allowedHosts);
    this.settings = Map.copyOf(settings);
  }
  
  public List<String> getAllowedHosts() {
    return allowedHosts;  // Already immutable, no copy needed
  }
  
  public Map<String, String> getSettings() {
    return settings;  // Already immutable, no copy needed
  }
}
```

## Stream Collection

**Prefer `toList()`** over `Collectors.toList()` unless stated otherwise.

```java
// Preferred - toList() returns an immutable list
var activeUsers = users.stream()
    .filter(User::isActive)
    .toList();

var userNames = users.stream()
    .map(User::getName)
    .toList();

// Avoid - unless you specifically need a mutable list
var activeUsers = users.stream()
    .filter(User::isActive)
    .collect(Collectors.toList());
```

Benefits of `toList()`:

- More concise

- Returns an immutable list by default (thread-safe)

- Better performance (optimized implementation)

- Clearer intent

**When to use `Collectors.toList()`:**

- When you explicitly need a mutable list

- When working with code that requires specific List implementations

```java
// Example where mutable list is needed
var mutableUsers = users.stream()
    .filter(User::isActive)
    .collect(Collectors.toList());

mutableUsers.add(newUser);  // This works because it's mutable
```
