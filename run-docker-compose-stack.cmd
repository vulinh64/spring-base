@echo off
SETLOCAL EnableDelayedExpansion

REM Check if Docker daemon is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    exit /b 1
)

docker compose down
docker rmi --force spring-base:1.0.0
docker compose up --detach

:: Configure Keycloak
call ./create-keycloak-data

if errorlevel 1 (
    echo Error: Keycloak configuration failed
    exit /b 1
)

ENDLOCAL