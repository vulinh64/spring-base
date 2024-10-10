## Table of Contents

<!-- TOC -->
  * [Table of Contents](#table-of-contents)
  * [Project setup (non-Docker approach)](#project-setup-non-docker-approach)
    * [Prerequisite](#prerequisite)
    * [Setting up database](#setting-up-database)
      * [SQL Script:](#sql-script)
    * [Setting up environment variables](#setting-up-environment-variables)
    * [Public key](#public-key)
    * [Private key](#private-key)
  * [Docker-based Setup](#docker-based-setup)
<!-- TOC -->

This is a demo project using Spring Boot to work as a blog site's backend.

## Project setup (non-Docker approach)

### Prerequisite

- JDK 17 or higher, you can download:
    - **Oracle JDK** from https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html;
    - or **GraalVM** for JDK 17 from https://www.graalvm.org/release-notes/JDK_17/
- PostgreSQL (https://www.postgresql.org/download/) installed on your local machine.
  - Because database changelogs use native PostgreSQL dialect, you will need to use PostgreSQL. If you planned to use another RDBMS, then you will have to rewrite the changelog files.

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
	
CREATE DATABASE myspringdatabase
    WITH
    OWNER = myspringdatabase
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

### Setting up environment variables

Spring Security uses these environment variables for authentication and authorization:

| Variable name | Description                                                          | Remarks                             |
|---------------|----------------------------------------------------------------------|-------------------------------------|
| `PUBLIC_KEY`  | The public key used to verify JWT Authorization                      | RSA public key (2048 bits or more)  |
| `PRIVATE_KEY` | The private key used to generate access token for authenticated user | RSA private key (2048 bits or more) |

For security reasons, I will not include the key pair here, and you will have to define them.

> For personal use, you can generate your own key pair using an online tool like: https://www.devglan.com/online-tools/rsa-encryption-decryption

> To facilitate environment variables in IntelliJ (which you should use frequently), you can use the plugin
EnvFile (https://plugins.jetbrains.com/plugin/7861-envfile) and enable the `.env` files when editing run configuration.

A simple `.env` file looks like this

```properties
PUBLIC_KEY=insert your public key here
PRIVATE_KEY=insert your private key here
#
# Other environment variables down below
#
```
Currently, an RSA public or private key is written in a single line, for example:

### Public key
```properties
PUBLIC_KEY=-----BEGIN PUBLIC KEY-----<single line here>-----END PUBLIC KEY-----
```

### Private key
```properties
PRIVATE_KEY=-----BEGIN PRIVATE KEY-----<single line here>-----END PRIVATE KEY-----
```

## Docker-based Setup

Run the project using Docker:
```shell
docker compose up
```

Verify at `http://localhost:8080/health` once containers are running.

This approach bypasses manual prerequisite installation.