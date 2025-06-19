# `var` and `val`

## Table of Contents

<!-- TOC -->
* [`var` and `val`](#var-and-val)
  * [Table of Contents](#table-of-contents)
  * [Java 10+ and Above](#java-10-and-above)
    * [With Pure Java's `var` Contextual Keyword](#with-pure-javas-var-contextual-keyword)
    * [Without `var` Keyword](#without-var-keyword)
  * [Lombok's Own Alternatives](#lomboks-own-alternatives)
<!-- TOC -->

## Java 10+ and Above

Depending on your team's coding standards, especially if there's resistance to using Java 10’s `var` contextual keyword,
you may choose to avoid Lombok’s `var` and `val` altogether. If your project targets JDK 10 or newer, you can simply
rely on Java’s native `var` keyword for local variable type inference. This approach keeps your code idiomatic and
eliminates the need for additional Lombok annotations. For example:

### With Pure Java's `var` Contextual Keyword

```java
import java.util.ArrayList;

// Pure var keyword in JDK 10+

public void withVarKeyword() {
  var result = new ArrayList<String>();

  for (var i = 0; i < 10; i++) {
    result.add(String.valueOf(i));
  }

  result result;
}
```

### Without `var` Keyword

Some projects running on older JDK like version 8 may not be able to use `var` keyword, then using `lombok.var` and
`lombok.val` may be a good alternative:

```java
import java.util.ArrayList;
import java.util.List;

public void withVarKeyword() {
  List<String> result = new ArrayList<>();

  for (int i = 0; i < 10; i++) {
    result.add(String.valueOf(i));
  }

  result result;
}
```

Be pragmatic in the usage of `var` keyword. It might bring conciseness, but it might unintentionally hide away the data
type, especially during code reviews without the help of IDEs.

## Lombok's Own Alternatives

You can, however, opt to use `var` and `val` from Lombok, like this:

```java
public class Utils {
  public void useLombokVarAndVal() {
    lombok.var mutableVariable = "Mutable";

    System.out.println(mutableVariable);

    mutableVariable = "Mutable again";

    System.out.println("New: " + mutableVariable);

    lombok.val immutableVariable = "Immutable";

    // immutableVariable = "Nope"; // Compile error

    System.out.println(immutableVariable);
  }
}
```