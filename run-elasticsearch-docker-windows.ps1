$CONTAINER_NAME = "standalone-elasticsearch"
$COMMAND = "docker run -d --name $CONTAINER_NAME -e ""xpack.security.enabled=false"" -e ""discovery.type=single-node"" -p 9200:9200 -p 9300:9300 elasticsearch:8.15.2"

$isContainerExists = docker ps -a | Select-String -Pattern $CONTAINER_NAME

if (-not $isContainerExists)
{
    Write-Host "Container [$CONTAINER_NAME] not existed, creating..."
    Invoke-Expression $COMMAND
    exit 0
}

$isContainerRunning = docker ps | Select-String -Pattern $CONTAINER_NAME

if (-not $isContainerRunning)
{
    Write-Host "Container [$CONTAINER_NAME] already stopped, restarting..."
    docker start $CONTAINER_NAME
    exit 0
}

Write-Host "Container [$CONTAINER_NAME] is already running..."