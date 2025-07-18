## Table of Contents

<!-- TOC -->
  * [Table of Contents](#table-of-contents)
  * [Project setup (non-Docker approach)](#project-setup-non-docker-approach)
    * [Prerequisite](#prerequisite)
    * [Setting up database](#setting-up-database)
      * [SQL Script:](#sql-script)
      * [Validate Liquibase's changelogs using Liquibase Maven Plugins](#validate-liquibases-changelogs-using-liquibase-maven-plugins)
    * [Setting up environment variables](#setting-up-environment-variables)
      * [Spring Security](#spring-security)
    * [Public key](#public-key)
    * [Private key](#private-key)
  * [Create Docker containers for your project](#create-docker-containers-for-your-project)
  * [Docker-based Setup](#docker-based-setup)
  * [Virtual threads](#virtual-threads)
    * [Spring Boot 3.2+](#spring-boot-32)
    * [Before Spring Boot 3.2](#before-spring-boot-32)
  * [Windows and Unix line separators](#windows-and-unix-line-separators)
  * [IntelliJ plugins](#intellij-plugins)
    * [**Save Actions X**](#save-actions-x)
    * [**JPA Buddy** (Freemium)](#jpa-buddy-freemium)
    * [**MapStruct Support** (if you plan to use MapStruct in your project](#mapstruct-support-if-you-plan-to-use-mapstruct-in-your-project)
    * [And the (in)famous **Sonarlint**](#and-the-infamous-sonarlint)
<!-- TOC -->

This is a demo project using Spring Boot to work as a blog site's backend.

## Project setup (non-Docker approach)

### Prerequisite

- JDK 21 or higher
- PostgreSQL (https://www.postgresql.org/download/) installed on your local machine.
    - Because database changelogs use native PostgreSQL dialect, you will need to use PostgreSQL. If you planned to use
      another RDBMS, then you will have to rewrite the changelog files.

### Setting up database

Run the following SQL Script to create a database with:

* name `myspringdatabase`
* username `myspringdatabase`
* password of `123456`

#### SQL Script:

```SQL
CREATE ROLE myspringdatabase WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    INHERIT
    NOREPLICATION
    CONNECTION LIMIT -1
    PASSWORD '123456';

CREATE DATABASE myspringdatabase WITH
    OWNER = myspringdatabase
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

#### Validate Liquibase's changelogs using Liquibase Maven Plugins

Create a Run configuration based on `liquibase:validate` goal, using following command:

```shell
clean compile liquibase:validate -Dliquibase.changeLogFile=src/main/resources/changelog.yaml -Dliquibase.url=jdbc:postgresql://localhost:5432/myspringdatabase -Dliquibase.username=postgres -Dliquibase.password=123456 -f pom.xml
```

Replace `-Dliquibase.username` and `-Dliquibase.password` with your actual username and password.

### Setting up environment variables

For `SPRING_PROFILES_ACTIVE`, you can use `development` most of the time.

#### Spring Security

Spring Security uses these environment variables for authentication and authorization:

| Variable name | Description                                                          | Remarks                             |
|---------------|----------------------------------------------------------------------|-------------------------------------|
| `PUBLIC_KEY`  | The public key used to verify JWT Authorization                      | RSA public key (2048 bits or more)  |
| `PRIVATE_KEY` | The private key used to generate access token for authenticated user | RSA private key (2048 bits or more) |

For security reasons, I will not include the key pair here, and you will have to define them.

> For personal use, you can generate your own key pair using an online tool
> like: https://www.devglan.com/online-tools/rsa-encryption-decryption

> To facilitate environment variables in IntelliJ (which you should use frequently), you can use the plugin
> EnvFile (https://plugins.jetbrains.com/plugin/7861-envfile) and enable the `.env` files when editing run
> configuration.

A simple `.env` file looks like this

```properties
PUBLIC_KEY=insert your public key here
PRIVATE_KEY=insert your private key here
#
# Other environment variables down below
#
```

You can check the example on `.env-example` file.

Currently, an RSA public or private key is written in a single line, for example:

### Public key

```properties
PUBLIC_KEY=-----BEGIN PUBLIC KEY-----<single line here>-----END PUBLIC KEY-----
```

### Private key

```properties
PRIVATE_KEY=-----BEGIN PRIVATE KEY-----<single line here>-----END PRIVATE KEY-----
```

## Create Docker containers for your project

This [script](initialize-postgres.cmd) automates the creation and management of Docker container for PostgreSQL. No
additional parameters are required. The script automatically detects the current state of your Docker environment and
takes appropriate action.

## Docker-based Setup

Run the project using this [script](run-docker.cmd).

Verify at `http://localhost:8443/health` once containers are running.

This approach bypasses manual prerequisite installation.

## Virtual threads

With the arrival of Java 21 and Spring Boot 3.2 onwards, you can use virtual threads to overcome the limited number of
platform threads managed by some sort of reactor library.

> **WORDS OF WARNING:**
>
> Test your application thoroughly, as the usage of virtual threads might (theoretically) break critical functions in
> your app. Use virtual threads with caution for older projects.

### Spring Boot 3.2+

From Spring 3.2 onwards, all you need is this line in your `application.properties` file:

```properties
spring.thread.virtuals.enabled=true
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

Make API call (without authorization) to

```text
/free/tax-calculator?basicSalary=5100000&totalSalary=6100000
```

to verify that Virtual Thread is indeed in use (use console log!)

## Windows and Unix line separators

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

## IntelliJ plugins

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

### **MapStruct Support** (if you plan to use MapStruct in your project

> https://plugins.jetbrains.com/plugin/10036-mapstruct-support

### And the (in)famous **Sonarlint**

> https://plugins.jetbrains.com/plugin/7973-sonarlint).

You may hold it dear as if it God's gospel, or anything else I really can't care more.
