## Table of Contents

<!-- TOC -->
  * [Table of Contents](#table-of-contents)
  * [Project setup](#project-setup)
    * [Prerequisite](#prerequisite)
    * [Setting up database](#setting-up-database)
      * [SQL Script:](#sql-script)
    * [Setting up environment variables](#setting-up-environment-variables)
<!-- TOC -->

## Project setup

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
    OWNER = notification
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
Currently, a pair of RSA public and private keys is written in a single line, for example:

```text
-----BEGIN PUBLIC KEY-----<single line here>-----END PUBLIC KEY-----
```
