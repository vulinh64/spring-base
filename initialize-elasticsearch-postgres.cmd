@echo off
SETLOCAL EnableDelayedExpansion

REM Define container names and commands
SET ES_CONTAINER_NAME=standalone-elasticsearch
SET ES_VOLUME_NAME=elasticsearch-volume
SET ES_COMMAND=docker run -d --name !ES_CONTAINER_NAME! -e "xpack.security.enabled=false" -e "discovery.type=single-node" -p 9200:9200 -p 9300:9300 -v !ES_VOLUME_NAME!:/usr/share/elasticsearch/data elasticsearch:8.15.2

SET PG_CONTAINER_NAME=standalone-postgresql
SET PG_VOLUME_NAME=postgres-volume
SET PG_COMMAND=docker run -d --name !PG_CONTAINER_NAME! -e "POSTGRES_USER=postgres" -e "POSTGRES_PASSWORD=123456" -e "POSTGRES_DB=myspringdatabase" -p 5432:5432 -v !PG_VOLUME_NAME!:/var/lib/postgresql/data postgres:latest

REM Check and start Elasticsearch container
echo Checking Elasticsearch container...
docker ps -a | findstr /C:"!ES_CONTAINER_NAME!" >nul

if errorlevel 1 (
    echo Container [!ES_CONTAINER_NAME!] not existed, creating with volume [!ES_VOLUME_NAME!]...
    !ES_COMMAND!
) else (
    docker ps | findstr /C:"!ES_CONTAINER_NAME!" >nul
    if errorlevel 1 (
        echo Container with the same name [!ES_CONTAINER_NAME!] stopped, restarting...
        docker start !ES_CONTAINER_NAME!
    ) else (
        echo Container [!ES_CONTAINER_NAME!] is already running...
    )
)

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

REM Wait for containers to be ready
echo.
echo Both containers have been started. Checking their status...
timeout /t 3 /nobreak >nul

REM Display running containers
echo.
echo Currently running containers:
docker ps

ENDLOCAL