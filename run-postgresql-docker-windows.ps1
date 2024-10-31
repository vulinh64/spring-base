$CONTAINER_NAME = "standalone-postgresql"
$COMMAND = "docker run -d --name $CONTAINER_NAME -e ""POSTGRES_USER=postgres"" -e ""POSTGRES_PASSWORD=123456"" -e ""POSTGRES_DB=myspringdatabase"" -p 5432:5432 postgres:latest"

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