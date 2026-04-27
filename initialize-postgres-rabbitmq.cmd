@echo off
SETLOCAL EnableDelayedExpansion

docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    exit /b 1
)

docker compose down
docker compose -f docker-compose-1.yml down

:: Create local repository for data classes
call ./create-data-classes

:: --- PostgreSQL Setup ---
SET PG_CONTAINER_NAME=postgresql
SET PG_VOLUME_NAME=postgresql-volume
SET POSTGRESQL_NAME=postgres
SET POSTGRESQL_TAG=18.3-alpine3.23
SET POSTGRESQL_IMAGE=%POSTGRESQL_NAME%:%POSTGRESQL_TAG%
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=spring-base" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql !POSTGRESQL_IMAGE!

echo Checking PostgreSQL container [%PG_CONTAINER_NAME%]...
docker ps -a | findstr /C:"!PG_CONTAINER_NAME!" >nul
if errorlevel 1 (
    echo Container [%PG_CONTAINER_NAME%] not existed, creating with volume [%PG_VOLUME_NAME%]...
    !PG_COMMAND!
) else (
    docker ps | findstr /C:"!PG_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container [%PG_CONTAINER_NAME%] stopped, restarting...
        docker start !PG_CONTAINER_NAME!
    ) else (
        echo Container [%PG_CONTAINER_NAME%] is already running.
    )
)

:: --- RabbitMQ Setup ---
SET RABBITMQ_CONTAINER_NAME=rabbitmq
SET RABBITMQ_NAME=rabbitmq
SET RABBITMQ_TAG=4.2.4-alpine
SET RABBITMQ_IMAGE=!RABBITMQ_NAME!:!RABBITMQ_TAG!
SET RABBITMQ_COMMAND=docker run --detach --name !RABBITMQ_CONTAINER_NAME! --hostname rabbitmq -e RABBITMQ_DEFAULT_USER=rabbitmq -e RABBITMQ_DEFAULT_PASS=123456 -p 5672:5672 -p 15672:15672 -v rabbitmq-volume:/var/lib/rabbitmq !RABBITMQ_IMAGE!

echo Checking RabbitMQ container [%RABBITMQ_CONTAINER_NAME%]...
docker ps -a | findstr /C:"!RABBITMQ_CONTAINER_NAME!" >nul
if errorlevel 1 (
    echo Container [%RABBITMQ_CONTAINER_NAME%] not existed, creating...
    !RABBITMQ_COMMAND!
) else (
    docker ps | findstr /C:"!RABBITMQ_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container [%RABBITMQ_CONTAINER_NAME%] stopped, restarting...
        docker start !RABBITMQ_CONTAINER_NAME!
    ) else (
        echo Container [%RABBITMQ_CONTAINER_NAME%] is already running.
    )
)

ENDLOCAL
