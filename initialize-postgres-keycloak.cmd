@echo off
SETLOCAL EnableDelayedExpansion

docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    exit /b 1
)

:: --- PostgreSQL Setup ---
SET PG_CONTAINER_NAME=postgresql
SET PG_VOLUME_NAME=postgresql-volume
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql/data postgres:18.0-alpine3.22

echo Checking PostgreSQL container [%PG_CONTAINER_NAME%]...
docker ps -a | findstr /C:"!PG_CONTAINER_NAME!" >nul
if errorlevel 1 (
    echo Container [%PG_CONTAINER_NAME%] not existed, creating with volume [%PG_VOLUME_NAME%]...
    !PG_COMMAND!
) else (
    docker ps | findstr /C:"!PG_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container with the same name [%PG_CONTAINER_NAME%] stopped, restarting...
        docker start !PG_CONTAINER_NAME!
    ) else (
        echo Container [%PG_CONTAINER_NAME%] is already running...
    )
)

:: KEYCLOAK_REALM and CLIENT_ID should match the values in application.properties

:: --- Keycloak Setup ---
set KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:26.4
set KEYCLOAK_CONTAINER=keycloak

:: application-properties.security.realm-name
set KEYCLOAK_REALM=spring-base

:: application-properties.security.client-name
set CLIENT_ID=spring-base-client

set KEYCLOAK_OVERLORD=admin
set KEYCLOAK_ADMIN_PASSWORD=123456

echo Stopping and removing old Keycloak containers/images...
docker container rm -f %KEYCLOAK_CONTAINER% 2>nul

echo Starting Keycloak container [%KEYCLOAK_CONTAINER%]...
docker run --name %KEYCLOAK_CONTAINER% --detach -p 8080:8080 -p 9000:9000 -e "KC_BOOTSTRAP_ADMIN_USERNAME=%KEYCLOAK_OVERLORD%" -e "KC_BOOTSTRAP_ADMIN_PASSWORD=%KEYCLOAK_ADMIN_PASSWORD%" -e "KC_HEALTH_ENABLED=true" -e "KC_METRICS_ENABLED=true" %KEYCLOAK_IMAGE% start-dev

docker cp HealthCheck %KEYCLOAK_CONTAINER%:/tmp/HealthCheck.java

:health_check
docker exec %KEYCLOAK_CONTAINER% sh -c "java /tmp/HealthCheck.java http://localhost:9000/health/live"
if !ERRORLEVEL!==0 (
    echo Keycloak is serviceable!
    goto after_health_check
) else (
    echo Waiting for Keycloak to be healthy...
    timeout /t 5 >nul
    goto health_check
)

:after_health_check

set KCADM_PATH=/opt/keycloak/bin/kcadm.sh

echo Configuring KCADM credentials...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% config credentials --server http://localhost:8080 --realm master --user %KEYCLOAK_OVERLORD% --password %KEYCLOAK_ADMIN_PASSWORD%

docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create realms -s realm=%KEYCLOAK_REALM% -s enabled=true

echo Creating client [%CLIENT_ID%]...

:: if anyone has any better idea to not use variable, let me know
set "CLIENT_CREATE_CMD=docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create clients -r %KEYCLOAK_REALM% -s clientId=%CLIENT_ID% -s enabled=true -s publicClient=true -s directAccessGrantsEnabled=true -i"
for /f "usebackq delims=" %%i in (`!CLIENT_CREATE_CMD!`) do (
    set CLIENT_UUID=%%i
)

:: --- Role and User Setup ---
SET ROLE_ADMIN=ADMIN
SET ROLE_POWER_USER=POWER_USER
SET ROLE_USER=USER

set ADMIN_USERNAME=administrator
set POWER_USER_USERNAME=power_user
set USER_USERNAME=user

docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create clients/%CLIENT_UUID%/roles -r %KEYCLOAK_REALM% -s name=%ROLE_ADMIN%
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create clients/%CLIENT_UUID%/roles -r %KEYCLOAK_REALM% -s name=%ROLE_POWER_USER%
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create clients/%CLIENT_UUID%/roles -r %KEYCLOAK_REALM% -s name=%ROLE_USER%

echo Creating admin user [%ADMIN_USERNAME%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create users -r %KEYCLOAK_REALM% -s username=%ADMIN_USERNAME% -s enabled=true -s email=administrator@email.com -s firstName=Administrator -s lastName=User
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% set-password -r %KEYCLOAK_REALM% --username %ADMIN_USERNAME% --new-password 123456

echo Assigning client role [%ROLE_ADMIN%] to user [%ADMIN_USERNAME%] in client [%CLIENT_ID%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% add-roles -r %KEYCLOAK_REALM% --uusername %ADMIN_USERNAME% --cclientid %CLIENT_ID% --rolename %ROLE_ADMIN%
echo Created user [%ADMIN_USERNAME%] with password 123456 and client role [%ROLE_ADMIN%] in client [%CLIENT_ID%]

echo Creating regular user [%USER_USERNAME%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create users -r %KEYCLOAK_REALM% -s username=%USER_USERNAME% -s enabled=true -s email=user@email.com -s firstName=Normal -s lastName=User
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% set-password -r %KEYCLOAK_REALM% --username %USER_USERNAME% --new-password 123456

echo Assigning client role [%ROLE_USER%] to user [%USER_USERNAME%] in client [%CLIENT_ID%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% add-roles -r %KEYCLOAK_REALM% --uusername %USER_USERNAME% --cclientid %CLIENT_ID% --rolename %ROLE_USER%
echo Created user [%USER_USERNAME%] with password 123456 and client role [%ROLE_USER%] in client [%CLIENT_ID%]

echo Creating power user [%POWER_USER_USERNAME%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% create users -r %KEYCLOAK_REALM% -s username=%POWER_USER_USERNAME% -s enabled=true -s email=power_user@email.com -s firstName=Power -s lastName=User
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% set-password -r %KEYCLOAK_REALM% --username %POWER_USER_USERNAME% --new-password 123456

echo Assigning client role [%ROLE_POWER_USER%] to user [%POWER_USER_USERNAME%] in client [%CLIENT_ID%]...
docker exec %KEYCLOAK_CONTAINER% %KCADM_PATH% add-roles -r %KEYCLOAK_REALM% --uusername %POWER_USER_USERNAME% --cclientid %CLIENT_ID% --rolename %ROLE_POWER_USER%
echo Created user [%POWER_USER_USERNAME%] with password 123456 and client role [%ROLE_POWER_USER%] in client [%CLIENT_ID%]

ENDLOCAL