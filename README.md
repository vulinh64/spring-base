# Quickstart the Application

## Table of Contents

<!-- TOC -->
* [Quickstart the Application](#quickstart-the-application)
  * [Table of Contents](#table-of-contents)
  * [Running the Container Stack for Local Development](#running-the-container-stack-for-local-development)
    * [Prerequisites](#prerequisites)
    * [Specifying Environment Variables](#specifying-environment-variables)
    * [Running the Required Containers](#running-the-required-containers)
  * [Running the Compose Stack](#running-the-compose-stack)
* [Additional Notes](#additional-notes)
  * [Virtual Threads](#virtual-threads)
    * [Spring Boot 3.2+](#spring-boot-32)
    * [Before Spring Boot 3.2](#before-spring-boot-32)
  * [Windows and Unix Line Separators](#windows-and-unix-line-separators)
  * [IntelliJ Plugins](#intellij-plugins)
    * [**Save Actions X**](#save-actions-x)
    * [**JPA Buddy** (Freemium)](#jpa-buddy-freemium)
    * [**MapStruct Support** (if you plan to use MapStruct in your project)](#mapstruct-support-if-you-plan-to-use-mapstruct-in-your-project)
    * [And the (in)famous **SonarLint**](#and-the-infamous-sonarlint)
* [JDK 25 Compact Object Headers](#jdk-25-compact-object-headers)
<!-- TOC -->

## Running the Container Stack for Local Development

### Prerequisites

* JDK 25+ (for coding and debugging, but not necessary if running the service in containers)
* Docker Desktop

### Specifying Environment Variables

The `SPRING_PROFILES_ACTIVE` variable is optional, but you can set it to `development` for local development.

You can either specify these environment variables in a `.env` file or set them directly in Run Configurations.

An example `.env` file can be copied from [this file](/.env-example).

### Running the Required Containers

You can run [this script (Windows only)](/initialize-postgres-keycloak.cmd) and it will start the required containers for local development: PostgreSQL and KeyCloak.

## Running the Compose Stack

You can run [this script](/run-docker.cmd) and it will build the service image and start the containers for you.

# Additional Notes

## Virtual Threads

With the arrival of Java 21 and Spring Boot 3.2 onwards, you can use virtual threads to overcome the limited number of
platform threads managed by some sort of reactor library.

> [!WARNING]
>
> Test your application thoroughly, as the usage of virtual threads might (theoretically) break critical functions in
your app. Use virtual threads with caution for older projects.

### Spring Boot 3.2+

From Spring Boot 3.2 onwards, all you need is this line in your `application.properties` file:

```properties
spring.threads.virtual.enabled=true
```

### Before Spring Boot 3.2

You will need to implement some workarounds to get the best from virtual threads if you are not using Spring Boot 3.2 (
but still using Java 21 and Spring Framework 6), like this:

```java
import java.util.concurrent.Executors;

import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@ConditionalOnProperty(prefix = "spring.threads", value = "virtual", havingValue = "true")
public class ThreadConfig {

  @Bean
  public AsyncTaskExecutor applicationTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  public TomcatProtocolHandlerCustomizer<ProtocolHandler>
      protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler ->
        protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
  }
}

```

Source: [Baeldung](https://www.baeldung.com/spring-6-virtual-threads)

Make an API call (without authorization) to

```text
/free/tax-calculator?basicSalary=5100000&totalSalary=6100000
```

to verify that a virtual thread is indeed in use (use the console log!).

## Windows and Unix Line Separators

When using Windows, your `mvnw` file might have Windows-style `CRLF` line endings instead of Unix-style `LF` when
committing, which can cause image build failures. To fix this issue using PowerShell, run:

```shell
(Get-Content mvnw -Raw).Replace("`r`n", "`n") | Set-Content mvnw -NoNewline -Force
```

In some cases, you may need to make the `mvnw` file executable by setting the `+x` permission attribute, like this:

```shell
git update-index --chmod=+x mvnw
```

You can use Windows Subsystem for Linux to achieve the same:

```shell
chmod +x mvnw
```

## IntelliJ Plugins

You might have mixed feelings about Google Java Format plugins, but I've found them very useful for catching those
tricky-to-spot errors – especially when dealing with nested parentheses. If you're curious, I'd definitely recommend
giving it a shot. Just head to the IntelliJ plugins menu, search for "Google Java Format," and you're all set.

Or here is the link:

> https://plugins.jetbrains.com/plugin/8527-google-java-format

Other useful plugins I can recommend:

### **Save Actions X**

> https://plugins.jetbrains.com/plugin/22113-save-actions-x

### **JPA Buddy** (Freemium)

> https://plugins.jetbrains.com/plugin/15075-jpa-buddy

### **MapStruct Support** (if you plan to use MapStruct in your project)

> https://plugins.jetbrains.com/plugin/10036-mapstruct-support

### And the (in)famous **SonarLint**

> https://plugins.jetbrains.com/plugin/7973-sonarlint

You may hold it dear as if it's God's gospel, or anything else, I really can't care more.

# JDK 25 Compact Object Headers

You can use the following JVM option to enable compact object headers in JDK 25 and later (expected to be enabled by default starting from JDK 26):

```
-XX:+UseCompactObjectHeaders
```