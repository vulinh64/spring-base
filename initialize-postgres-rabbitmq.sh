#!/bin/sh

# Check if Docker is running
docker info >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Docker daemon is not running."
    echo "Please start Docker and run this script again."
    exit 1
fi

docker compose down
docker compose -f docker-compose-1.yml down

# Create local repository for data classes
chmod +x ./create-data-classes.sh
./create-data-classes.sh

# --- PostgreSQL Setup ---
PG_CONTAINER_NAME="postgresql"
PG_VOLUME_NAME="postgresql-volume"
POSTGRESQL_NAME="postgres"
POSTGRESQL_TAG="18.3-alpine3.23"
POSTGRESQL_IMAGE="${POSTGRESQL_NAME}:${POSTGRESQL_TAG}"
PG_COMMAND="docker run -d --name ${PG_CONTAINER_NAME} -e \"POSTGRES_USER=postgres\" -e \"POSTGRES_PASSWORD=123456\" -e \"POSTGRES_DB=spring-base\" -p 5432:5432 -v ${PG_VOLUME_NAME}:/var/lib/postgresql ${POSTGRESQL_IMAGE}"

echo "Checking PostgreSQL container [${PG_CONTAINER_NAME}]..."
if ! docker ps -a | grep -q "${PG_CONTAINER_NAME}"; then
    echo "Container [${PG_CONTAINER_NAME}] not existed, creating with volume [${PG_VOLUME_NAME}]..."
    eval "${PG_COMMAND}"
else
    if ! docker ps | grep -q "${PG_CONTAINER_NAME}"; then
        echo "Container [${PG_CONTAINER_NAME}] stopped, restarting..."
        docker start "${PG_CONTAINER_NAME}"
    else
        echo "Container [${PG_CONTAINER_NAME}] is already running."
    fi
fi

# --- RabbitMQ Setup ---
RABBITMQ_CONTAINER_NAME="rabbitmq"
RABBITMQ_NAME="rabbitmq"
RABBITMQ_TAG="4.2.4-alpine"
RABBITMQ_IMAGE="${RABBITMQ_NAME}:${RABBITMQ_TAG}"
RABBITMQ_COMMAND="docker run --detach --name ${RABBITMQ_CONTAINER_NAME} --hostname rabbitmq -e RABBITMQ_DEFAULT_USER=rabbitmq -e RABBITMQ_DEFAULT_PASS=123456 -p 5672:5672 -p 15672:15672 -v rabbitmq-volume:/var/lib/rabbitmq ${RABBITMQ_IMAGE}"

echo "Checking RabbitMQ container [${RABBITMQ_CONTAINER_NAME}]..."
if ! docker ps -a | grep -q "${RABBITMQ_CONTAINER_NAME}"; then
    echo "Container [${RABBITMQ_CONTAINER_NAME}] not existed, creating..."
    eval "${RABBITMQ_COMMAND}"
else
    if ! docker ps | grep -q "${RABBITMQ_CONTAINER_NAME}"; then
        echo "Container [${RABBITMQ_CONTAINER_NAME}] stopped, restarting..."
        docker start "${RABBITMQ_CONTAINER_NAME}"
    else
        echo "Container [${RABBITMQ_CONTAINER_NAME}] is already running."
    fi
fi
