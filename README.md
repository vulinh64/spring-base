# spring-base

## Description

A proof-of-concept backend for a blog system. It offers basic management of the core blog entities:

- **Posts**

- **Comments**

- **Tags**

- **Categories**

## Requirements for local debugging

| Component            | Notes                                                                                                                |
|----------------------|----------------------------------------------------------------------------------------------------------------------|
| JDK 25               | Required to build and run.                                                                                           |
| Maven                | The wrapper (`mvnw` / `mvnw.cmd`) is included — no global install needed.                                            |
| PostgreSQL           | Application database.                                                                                                |
| RabbitMQ             | Message broker (key-rotation events, outbound notifications).                                                        |
| Authorization server | [vulinh64/spring-base-auth](https://github.com/vulinh64/spring-base-auth) — issues and validates JWTs.               |
| Commons library      | [vulinh64/spring-base-commons](https://github.com/vulinh64/spring-base-commons) — shared data classes and utilities. |

## Setup

### Step 1 — Install the commons library

The commons artifact is published as a GitHub release and must be installed into your local Maven repository before the project can resolve its dependencies.

- **Windows:** run [`create-data-classes.cmd`](./create-data-classes.cmd)

- **Linux / macOS:** run [`create-data-classes.sh`](./create-data-classes.sh)

### Step 2 — Initialize PostgreSQL and RabbitMQ containers

Only needed when running this service **standalone**. In normal operation the auth server is responsible for provisioning the shared schema (for itself, for this project, and for other downstream services), so this step is usually already done by the time you start `spring-base`.

- **Windows:** run [`initialize-postgres-rabbitmq.cmd`](./initialize-postgres-rabbitmq.cmd)

- **Linux / macOS:** run [`initialize-postgres-rabbitmq.sh`](./initialize-postgres-rabbitmq.sh)

## Configuration

Sensible defaults are already wired up in [`application.yaml`](./src/main/resources/application.yaml), so the app boots out of the box against the containers from Step 2. Override any of the following environment variables when the defaults do not match your setup:

| Environment variable | Default                                   | Remark                                                                           |
|----------------------|-------------------------------------------|----------------------------------------------------------------------------------|
| `SERVER_PORT`        | `8088`                                    | HTTP port this service listens on.                                               |
| `JWT_ISSUER_URI`     | `http://localhost:8080`                   | Base URI of the auth server. Used for `iss` claim validation.                    |
| `JWT_JWK_SET_URI`    | `${JWT_ISSUER_URI}/.well-known/jwks.json` | JWKS endpoint. Override only if JWKS is served from a different host.            |
| `JWT_CLIENT_NAME`    | `spring-base`                             | Expected `aud` claim — must match this service's `client_id` on the auth server. |
| `POSTGRES_HOST`      | `localhost`                               | PostgreSQL host.                                                                 |
| `POSTGRES_PORT`      | `5432`                                    | PostgreSQL port.                                                                 |
| `POSTGRES_DB`        | `spring-base`                             | Database name.                                                                   |
| `POSTGRES_USER`      | `postgres`                                | Database user.                                                                   |
| `POSTGRES_PASSWORD`  | `123456`                                  | Database password.                                                               |
| `RABBITMQ_HOST`      | `localhost`                               | RabbitMQ host.                                                                   |
| `RABBITMQ_PORT`      | `5672`                                    | RabbitMQ AMQP port.                                                              |
| `RABBITMQ_USERNAME`  | `rabbitmq`                                | RabbitMQ user.                                                                   |
| `RABBITMQ_PASSWORD`  | `123456`                                  | RabbitMQ password.                                                               |
