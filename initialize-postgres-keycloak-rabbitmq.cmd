@echo off
SETLOCAL EnableDelayedExpansion

docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    exit /b 1
)

:: Initialize data dependency
call ./create-data-classes

:: --- PostgreSQL Setup ---
SET PG_CONTAINER_NAME=postgresql
SET PG_VOLUME_NAME=postgresql-volume
SET POSTGRESQL_NAME=postgres
SET POSTGRESQL_TAG=18.1-alpine3.22
SET POSTGRESQL_IMAGE=%POSTGRESQL_NAME%:%POSTGRESQL_TAG%
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql/data !POSTGRESQL_IMAGE!

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
SET RABBITMQ_TAG=4.2.1-management-alpine
SET RABBITMQ_IMAGE=!RABBITMQ_NAME!:!RABBITMQ_TAG!
SET RABBITMQ_COMMAND=docker run --detach --name !RABBITMQ_CONTAINER_NAME! --hostname rabbitmq -e RABBITMQ_DEFAULT_USER=rabbitmq -e RABBITMQ_DEFAULT_PASS=123456 -p 5672:5672 -p 15672:15672 !RABBITMQ_IMAGE!

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

:: KEYCLOAK_REALM and CLIENT_NAME should match the values in application.properties

:: --- Keycloak Setup ---
SET KEYCLOAK_NAME=quay.io/keycloak/keycloak
SET KEYCLOAK_TAG=26.4.6
SET KEYCLOAK_IMAGE=%KEYCLOAK_NAME%:%KEYCLOAK_TAG%
SET KEYCLOAK_CONTAINER=keycloak

set KEYCLOAK_OVERLORD=admin
set KEYCLOAK_ADMIN_PASSWORD=123456

SET KEYCLOAK_COMMAND=docker run --name !KEYCLOAK_CONTAINER! --detach -p 8080:8080 -p 9000:9000 -e "KC_BOOTSTRAP_ADMIN_USERNAME=!KEYCLOAK_OVERLORD!" -e "KC_BOOTSTRAP_ADMIN_PASSWORD=!KEYCLOAK_ADMIN_PASSWORD!" -e "KC_HEALTH_ENABLED=true" -e "KC_METRICS_ENABLED=true" !KEYCLOAK_IMAGE! start-dev

echo Checking Keycloak container [%KEYCLOAK_CONTAINER%]...
docker ps -a | findstr /C:"!KEYCLOAK_CONTAINER!" >nul
if errorlevel 1 goto create_and_configure_keycloak

:: Container exists, check if it is running then exit
docker ps | findstr /C:"!KEYCLOAK_CONTAINER!" >nul
if not errorlevel 1 (
    echo Container [%KEYCLOAK_CONTAINER%] is already running.
) else (
    echo Container [%KEYCLOAK_CONTAINER%] existed but stopped, restarting...
    docker start !KEYCLOAK_CONTAINER!
)
goto :eof

:create_and_configure_keycloak
echo Container [%KEYCLOAK_CONTAINER%] not existed, creating...
!KEYCLOAK_COMMAND!

docker cp HealthCheck %KEYCLOAK_CONTAINER%:/tmp/HealthCheck.java

set RETRY_COUNT=0
set MAX_RETRIES=12
:health_check
docker exec %KEYCLOAK_CONTAINER% sh -c "java /tmp/HealthCheck.java http://localhost:9000/health/live"
if !ERRORLEVEL!==0 (
    echo Keycloak is serviceable!
    goto after_health_check
) else (
    set /a RETRY_COUNT+=1
    if !RETRY_COUNT! geq !MAX_RETRIES! (
        echo Error: Keycloak is not healthy after !MAX_RETRIES! retries.
        exit /b 1
    )
    echo Waiting for Keycloak to be healthy... !RETRY_COUNT!/!MAX_RETRIES!
    timeout /t 5 >nul
    goto health_check
)

:after_health_check

:: Configure Keycloak
call ./create-keycloak-data
if errorlevel 1 (
    echo Error: Keycloak configuration failed
    exit /b 1
)

ENDLOCAL