## Before Java 21

This is how our "Hello, World!" program has (always) been looked like:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

What we can see from the above piece of codes?

- Too verbose, and it will certainly scare away newcomers.
- What is the meaning of `public static void` anyway?
- And why do we need `String[] args` if we are not going to use it?
- Why just a simple function like printing a text to the console looked so overly complicated like `System.out.println`?

Things started to change since Java 21.

## Proposals for unnamed classes and instance main method

With Java 21, the infamous "Hello, World!" can be re-written to look like this:

```java
void main() {
    println("Hello, World!");
}
```

The above program looked shorter and more neat than before. This is how the new `Hello, World` has become:

- `public static` no longer plagued us.
- `String[] args` was also gone, because we do not need them in our simple program.
- The terrifying `System.out.println` has been replaced by a simple `println`. The method was implicitly imported from `java.io.IO` package (acts like `java.lang` package that does not need to be implicitly imported).

You can learn more about the new changes in this JEP: https://openjdk.org/jeps/445

A side note: the package `java.io.IO` contains several helpful methods:

- `println`: works just like good old `System.out.println`.
- `print`: like `System.out.print` (without the new line character `\n` at the end).
- `readln`: Display a text to the console as if the `print` method was used, then accept the input user enters in the console.

For example: 

```java
void main() {
    var name = readln("Enter your name: ");
    println("Hi " + name);
}
```

Is roughly equivalent to this code:

```java
public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        var name = scanner.nextLine();
        System.out.println("Hi " + name);
    }
}
```

TBC...