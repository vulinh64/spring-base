# Lombok Installation

## Table of Contents

<!-- TOC -->
* [Lombok Installation](#lombok-installation)
  * [Table of Contents](#table-of-contents)
  * [Step 1: Transitive Dependency](#step-1-transitive-dependency)
  * [Step 2: Annotation Processing Configuration](#step-2-annotation-processing-configuration)
<!-- TOC -->

## Step 1: Transitive Dependency

Add the following dependency into your project's `pom.xml` file:

```xml

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
</dependency>
```

The property `lombok.version` is mostly defined by Spring Boot parent POM, but you can also define the value manually.
The latest version at the time of this writing is `1.18.38` (2025-06-17).

> You can find the latest version of Lombok from here:
>
> https://mvnrepository.com/artifact/org.projectlombok/lombok

## Step 2: Annotation Processing Configuration

You will also need to configure annotation processors if your project falls under any of the following scenarios:

* Your project is using JDK 23 or later (JDK 25 is expected to become the standard upon its release in September 2025).

---

* Your project uses JDK 9 or above and incorporates the Java Module System (i.e., contains a `module-info.java` file), a
  relatively uncommon setup in most projects.

---

* Your project includes additional annotation processors such as MapStruct, QueryDSL, Hibernate ModelGen, or others. In
  these cases, proper configuration ensures all processors function correctly and do not conflict with each other.

Add the following configuration to `maven-compile-plugin` plugin:

```xml
<!-- You mostly don't need to explicitly provide a value for ${lombok.version} -->
<annotationProcessorPaths>
  <path>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
  </path>

  <!-- Other annotation processors -->
</annotationProcessorPaths>
```

So, the full `maven-compile-plugin` declaration will look like this:

```xml

<build>
  <plugins>
    <!-- Other plugins -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>${maven-compiler-plugin.version}</version>
      <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <annotationProcessorPaths>
          <path>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
          </path>

          <!-- Other annotation processors -->
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

In this example:

* `java.version` = `17`
* `maven-compiler-plugin.version` = `3.14.0`