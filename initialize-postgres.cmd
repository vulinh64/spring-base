@echo off
SETLOCAL EnableDelayedExpansion

REM Check if Docker daemon is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    pause
    exit /b 1
)

REM Define container name and command
SET PG_CONTAINER_NAME=standalone-postgresql
SET PG_VOLUME_NAME=postgres-volume
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql/data postgres:18.0-alpine3.22

REM Check and start PostgreSQL container
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

REM Wait for container to be ready

timeout /t 3 /nobreak >nul

REM Display running containers
echo.
echo Currently running containers:
docker ps

ENDLOCAL