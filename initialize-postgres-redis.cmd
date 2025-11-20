@echo off
SETLOCAL EnableDelayedExpansion

docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    pause
    exit /b 1
)

SET PG_CONTAINER_NAME=standalone-postgresql
SET PG_VOLUME_NAME=postgres-volume
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql/data postgres:18.0-alpine3.22

echo Checking PostgreSQL container...
docker ps -a | findstr /C:"!PG_CONTAINER_NAME!" >nul

if errorlevel 1 (
    echo Container [!PG_CONTAINER_NAME!] not existed, creating with volume [!PG_VOLUME_NAME!]...
    !PG_COMMAND!
) else (
    docker ps | findstr /C:"!PG_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container with the same name [!PG_CONTAINER_NAME!] stopped, restarting...
        docker start !PG_CONTAINER_NAME!
    ) else (
        echo Container [!PG_CONTAINER_NAME!] is already running...
    )
)

SET REDIS_CONTAINER_NAME=standalone-redis
SET REDIS_VOLUME_NAME=redis-volume
SET REDIS_COMMAND=docker run --detach --name !REDIS_CONTAINER_NAME! -v !REDIS_VOLUME_NAME!:/data -p 6379:6379 redis:8.2.3-bookworm redis-server --save 60 1 --loglevel warning

echo Checking Redis container...
docker ps -a | findstr /C:"!REDIS_CONTAINER_NAME!" >nul

if errorlevel 1 (
    echo Container [!REDIS_CONTAINER_NAME!] not existed, creating with volume [!REDIS_VOLUME_NAME!]...
    !REDIS_COMMAND!
) else (
    docker ps | findstr /C:"!REDIS_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container with the same name [!REDIS_CONTAINER_NAME!] stopped, restarting...
        docker start !REDIS_CONTAINER_NAME!
    ) else (
        echo Container [!REDIS_CONTAINER_NAME!] is already running...
    )
)

echo.
echo Currently running containers:
docker ps

ENDLOCAL