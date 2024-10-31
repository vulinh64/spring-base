@echo off
SET CONTAINER_NAME=standalone-postgresql
SET COMMAND=docker run -d --name %CONTAINER_NAME% -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 postgres:latest

docker ps -a | findstr /C:"%CONTAINER_NAME%" >nul

if errorlevel 1 (
    echo Container [%CONTAINER_NAME%] not existed, creating...
    %COMMAND%
) else (
    docker ps | findstr /C:"%CONTAINER_NAME%" >nul
    if errorlevel 1 (
        echo Container with the same name [%CONTAINER_NAME%] stopped, restarting...
        docker start %CONTAINER_NAME%
    ) else (
        echo Container [%CONTAINER_NAME%] is already running...
    )
)