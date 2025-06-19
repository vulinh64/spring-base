# `@Locked` Annotations

## Table of Contents

<!-- TOC -->
* [`@Locked` Annotations](#locked-annotations)
  * [Table of Contents](#table-of-contents)
  * [Foreword](#foreword)
    * [💡 Tips](#-tips)
  * [Usage](#usage)
    * [Lombok code](#lombok-code)
    * [Vanilla Java code](#vanilla-java-code)
<!-- TOC -->

## Foreword

Introduced in Lombok 1.18.32, the `@Locked` annotation enables the injection of Java Locks, specifically `ReentrantLock`
and `ReadWriteLock`, into your code. This feature is particularly useful when working with Virtual Threads, introduced
in JDK 21 and onwards.

When using Virtual Threads, relying on the synchronized keyword or invoking native C/C++ code can cause the thread to
become pinned to its underlying platform (carrier) thread. This undermines the core benefit of Virtual Threads: their
ability to be mounted and unmounted from carrier threads to maximize concurrency and throughput.

To avoid this pinning issue, Java Locks provide a more compatible alternative to `synchronized` keyword. Although this
problem is resolved in JDK 24 (see [JEP 491](https://openjdk.org/jeps/491)), many organizations are unlikely to adopt
JDK 25 immediately upon its release in September 2025, primarily due to compatibility concerns.

In most cases, this annotation is preferable over Lombok's own `@Synchronized`.

### 💡 Tips

Spring Boot 3.2+ enables you to enable the Usage of Virtual Threads with a simple configuration:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

## Usage

### Lombok code

```java
import lombok.Locked;

public class YourBusinessService {

  @Locked.Read
  public void doRead() {
    // your business code
  }

  @Locked.Write
  public void doWrite() {
    // your business code
  }

  @Locked
  public void execute() {
    // your business code
  }
}
```

As for the time of writing (2025-06-17), it is currently not possible to specify fairness of the locks, and you will
have to resort to Java vanilla code for better fine-tuning, or doing something like this:

```java
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class YourBusinessService {

  private final Lock lock = new ReentrantLock(true);

  public void execute() {
    // Attempt to acquire the lock only if it's currently available. 
    // This is particularly useful in scenarios where scheduled tasks (e.g., cron jobs) might overlap:
    // such as one job starting at 08:01 AM while the previous execution is still running.
    if (!lock.tryLock(5000L, TimeUnit.MILLISECONDS)) {
      log.warn("Current task is still running...");

      return;
    }

    try {
      // your business code
    } finally {
      lock.unlock();
    }
  }
}
```

### Vanilla Java code

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class YourBusinessService {

  private final Lock lock = new ReentrantLock();
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  private final ReadLock readLock = readWriteLock.readLock();
  private final WriteLock writeLock = readWriteLock.writeLock();

  public void doRead() {
    readLock.lock();

    try {
      // your business code
    } finally {
      readLock.unlock();
    }
  }

  public void doWrite() {
    writeLock.lock();

    try {
      // your business code
    } finally {
      writeLock.unlock();
    }
  }

  public void execute() {
    lock.lock();

    try {
      // your business code
    } finally {
      lock.unlock();
    }
  }
}
```
