# Quickstart the Application

## Table of Contents

<!-- TOC -->
* [Quickstart the Application](#quickstart-the-application)
  * [Table of Contents](#table-of-contents)
  * [Running the Container Stack for Local Development](#running-the-container-stack-for-local-development)
    * [Prerequisites](#prerequisites)
    * [Required External Dependency](#required-external-dependency)
    * [Dependency Version](#dependency-version)
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
  * [Share the `.m2` Folder to WSL2 Ubuntu](#share-the-m2-folder-to-wsl2-ubuntu)
    * [Create a "Link" from WSL2 Ubuntu to Windows's `.m2` Folder](#create-a-link-from-wsl2-ubuntu-to-windowss-m2-folder)
    * [Unlink](#unlink)
<!-- TOC -->

## Running the Container Stack for Local Development

### Prerequisites

* JDK 25+ (for coding and debugging, but not necessary if running the service in containers)
* Docker Desktop

### Required External Dependency

The project uses the external dependency [spring-base-commons](https://github.com/vulinh64/spring-base-commons).

* For Windows, run [this script](/create-data-classes.cmd)

* For Linux, run [this script](/create-data-classes.sh)

  * Run `chmod +x ./create-data-classes.sh` if you don't have permission to execute the shell file.

Check if your `pom.xml` contains these lines in the `<dependencies>` section:

```xml
<dependency>
  <groupId>com.vulinh</groupId>
  <artifactId>spring-base-commons</artifactId>
  <version>${spring-base-commons.version}</version>
</dependency>
```

### Dependency Version

This project uses `spring-base-commons` as an external dependency (see above). If you want to change the version, check the following locations:

* [`pom.xml` file](pom.xml) - find the property `spring-base-commons.version`.

* Environment variable `SPRING_BASE_COMMONS_VERSION` in:

  * [Dockerfile](/Dockerfile)

  * [create-data-classes.cmd](/create-data-classes.cmd) (Windows)

  * [create-data-classes.sh](/create-data-classes.sh) (Linux)

  * [GitHub Actions workflows](/.github/workflows/unit-test.yml) (the variable is `${{ env.SPRING_BASE_COMMONS_VERSION }}`)

### Specifying Environment Variables

The `SPRING_PROFILES_ACTIVE` variable is optional, but you can set it to `development` for local development.

You can either specify these environment variables in a `.env` file or set them directly in Run Configurations.

An example `.env` file can be copied from [this file](/.env-example).

### Running the Required Containers

You can run [this script (Windows only)](/initialize-postgres-keycloak-rabbitmq.cmd) or [this script (Linux only)](/initialize-postgres-keycloak-rabbitmq.sh), and it will start the required containers for local development: PostgreSQL and Keycloak.

> Both scripts have already handled the external dependency for you. See the [Required External Dependency](#required-external-dependency) section for more information.

## Running the Compose Stack

You can run [this script (Windows only)](/run-docker-compose-stack.cmd), and it will build the service image and start the containers for you.

> Again, both scripts have already handled the external dependency for you.

# Additional Notes

## Virtual Threads

With the arrival of Java 21 and Spring Boot 3.2 onwards, you can use virtual threads to overcome the limited number of platform threads managed by reactor libraries.

> [!WARNING]
>
> Test your application thoroughly, as the usage of virtual threads might (theoretically) break critical functions in your app. Use virtual threads with caution for older projects.

### Spring Boot 3.2+

From Spring Boot 3.2 onwards, all you need is this line in your `application.properties` file:

```properties
spring.threads.virtual.enabled=true
```

### Before Spring Boot 3.2

You will need to implement some workarounds to get the best from virtual threads if you are not using Spring Boot 3.2 (but still using Java 21 and Spring Framework 6), like this:

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

to verify that a virtual thread is indeed in use (check the console log).

## Windows and Unix Line Separators

When using Windows, your `mvnw` file might have Windows-style `CRLF` line endings instead of Unix-style `LF` when committing, which can cause image build failures. To fix this issue using PowerShell, run:

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

You might have mixed feelings about Google Java Format plugins, but I've found them very useful for catching those tricky-to-spot errors – especially when dealing with nested parentheses. If you're curious, I'd definitely recommend giving it a try. Just head to the IntelliJ plugins menu, search for "Google Java Format," and you're all set.

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

You may hold it dear as if it's the gospel, or not, I really couldn't care less.

# JDK 25 Compact Object Headers

You can use the following JVM option to enable compact object headers in JDK 25 and later (expected to be enabled by default starting from JDK 26):

```
-XX:+UseCompactObjectHeaders
```

## Share the `.m2` Folder to WSL2 Ubuntu

> [!WARNING]
>
> This will introduce some performance penalty due to the overhead of translating Windows's NTFS file system calls to Linux's ext4 file system calls. However, in return, it saves you time by avoiding the need to download or store dependencies multiple times in both Windows and WSL2 Ubuntu.

### Create a "Link" from WSL2 Ubuntu to Windows's `.m2` Folder

```shell
# Remove existing .m2 folder if it exists
rm -rf ~/.m2

# Link the .m2 folder from Windows to WSL2 Ubuntu
ln -s /mnt/c/Users/[your Windows user name]/.m2 ~/.m2
```

Replace `[your Windows user name]` with your actual Windows username.

### Unlink

```shell
rm -f ~/.m2
```