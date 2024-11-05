@echo off
SET CONTAINER_NAME=standalone-elasticsearch
SET COMMAND=docker run -d --name %CONTAINER_NAME% -e "xpack.security.enabled=false" -e "discovery.type=single-node" -p 9200:9200 -p 9300:9300 elasticsearch:8.15.2

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