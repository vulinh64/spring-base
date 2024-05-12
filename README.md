## Table of Contents

<!-- TOC -->
  * [Table of Contents](#table-of-contents)
  * [Project setup](#project-setup)
    * [Prerequisite](#prerequisite)
    * [Setting up database](#setting-up-database)
    * [Setting up environment variables](#setting-up-environment-variables)
  * [Features in this demo project:](#features-in-this-demo-project)
  * [Basic CRUD Service](#basic-crud-service)
<!-- TOC -->

## Project setup

### Prerequisite

- JDK 17 or higher, you can download
    - Oracle JDK from https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html;
    - or GraalVM for JDK 17 from https://www.graalvm.org/release-notes/JDK_17/
- PostgreSQL (https://www.postgresql.org/download/) installed on your local machine.

### Setting up database

Run the following SQL Script to create a database name `myspringdatabase` with an username `myspringdatabase` and a
sample password of `123456`:

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
	
CREATE DATABASE myspringdatabase
    WITH
    OWNER = notification
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

### Setting up environment variables

UPDATED:

> **You will also need to define your own `SENTRY_DNS` value if you want to make use of Sentry.**

You will need two environment variables used by Spring Security for authentication/authorization purpose:

| Variable name | Description                                                          | Remarks                             |
|---------------|----------------------------------------------------------------------|-------------------------------------|
| `PUBLIC_KEY`  | The public key used to verify JWT Authorization                      | RSA public key (2048 bits or more)  |
| `PRIVATE_KEY` | The private key used to generate access token for authenticated user | RSA private key (2048 bits or more) |
| `SENTRY_DNS`  | Sentry DNS for Sentry APIs usage                                     |                                     |

> For security reasons, I will not include the key pair here.

> You can generate your own key for personal use here: https://www.devglan.com/online-tools/rsa-encryption-decryption

To help facilitate environment variables in IntelliJ (which you should use frequently), you can use the plugin
EnvFile (https://plugins.jetbrains.com/plugin/7861-envfile) and enable the `.env` files when editing run configuration.

A .env file looks like this

```properties
PUBLIC_KEY=insert your public key here
PRIVATE_KEY=insert your private key here
#
# Other environment variables down below
#
```

## Features in this demo project:

- Liquibase, a database migration tool (PostgreSQL)
- Mapstruct (and annotation processor)
- Spring Security & JWT (RSA 2048 bit) validation and generation
- Localization support via multi resources bundles
- Lombok (heavily make use of `@Accessor(chain = true)` that helps create chaining setters)
- QueryDSL, a tool to help create powerful JPQL statements
- Role hierarchy: in this demo project: ADMIN > POWER_USER > USER

> To be continued...

## Basic CRUD Service

> To be revamped...