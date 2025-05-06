@echo off

REM Check if Docker daemon is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker daemon is not running.
    echo Please start Docker Desktop or Docker service and run this script again.
    pause
    exit /b 1
)

docker compose down
docker rmi --force spring-base
docker compose up --detach