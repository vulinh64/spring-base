#!/bin/sh

# Check if Docker is running
docker info >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Docker daemon is not running."
    echo "Please start Docker and run this script again."
    exit 1
fi

chmod +x ./create-data-classes.sh

./create-data-classes.sh

# --- PostgreSQL Setup ---
PG_CONTAINER_NAME="postgresql"
PG_VOLUME_NAME="postgresql-volume"
POSTGRESQL_NAME="postgres"
POSTGRESQL_TAG="18.1-alpine3.22"
POSTGRESQL_IMAGE="${POSTGRESQL_NAME}:${POSTGRESQL_TAG}"
PG_COMMAND="docker run -d --name ${PG_CONTAINER_NAME} -e \"POSTGRES_USER=postgres\" -e \"POSTGRES_PASSWORD=123456\" -e \"POSTGRES_DB=myspringdatabase\" -p 5432:5432 -v ${PG_VOLUME_NAME}:/var/lib/postgresql/data ${POSTGRESQL_IMAGE}"

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
RABBITMQ_TAG="4.2.1-management-alpine"
RABBITMQ_IMAGE="${RABBITMQ_NAME}:${RABBITMQ_TAG}"
RABBITMQ_COMMAND="docker run --detach --name ${RABBITMQ_CONTAINER_NAME} --hostname rabbitmq -e RABBITMQ_DEFAULT_USER=rabbitmq -e RABBITMQ_DEFAULT_PASS=123456 -p 5672:5672 -p 15672:15672 ${RABBITMQ_IMAGE}"

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

# KEYCLOAK_REALM and CLIENT_NAME should match the values in application.properties
# --- Keycloak Setup ---
KEYCLOAK_NAME="quay.io/keycloak/keycloak"
KEYCLOAK_TAG="26.4.6"
KEYCLOAK_IMAGE="${KEYCLOAK_NAME}:${KEYCLOAK_TAG}"
KEYCLOAK_CONTAINER="keycloak"
KEYCLOAK_OVERLORD="admin"
KEYCLOAK_ADMIN_PASSWORD="123456"

KEYCLOAK_COMMAND="docker run --name ${KEYCLOAK_CONTAINER} --detach -p 8080:8080 -p 9000:9000 -e \"KC_BOOTSTRAP_ADMIN_USERNAME=${KEYCLOAK_OVERLORD}\" -e \"KC_BOOTSTRAP_ADMIN_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD}\" -e \"KC_HEALTH_ENABLED=true\" -e \"KC_METRICS_ENABLED=true\" ${KEYCLOAK_IMAGE} start-dev"

echo "Checking Keycloak container [${KEYCLOAK_CONTAINER}]..."

# Container exists, check if running and exit early
if docker ps -a | grep -q "${KEYCLOAK_CONTAINER}"; then
    if docker ps | grep -q "${KEYCLOAK_CONTAINER}"; then
        echo "Container [${KEYCLOAK_CONTAINER}] is already running."
    else
        echo "Container [${KEYCLOAK_CONTAINER}] existed but stopped, restarting..."
        docker start "${KEYCLOAK_CONTAINER}"
    fi
    exit 0
fi

# Container doesn't exist, create and configure
echo "Container [${KEYCLOAK_CONTAINER}] not existed, creating..."
eval "${KEYCLOAK_COMMAND}"

docker cp HealthCheck "${KEYCLOAK_CONTAINER}":/tmp/HealthCheck.java

RETRY_COUNT=0
MAX_RETRIES=12

# Health check loop
while true; do
    docker exec "${KEYCLOAK_CONTAINER}" sh -c "java /tmp/HealthCheck.java http://localhost:9000/health/live"
    if [ $? -eq 0 ]; then
        echo "Keycloak is serviceable!"
        break
    else
        RETRY_COUNT=$((RETRY_COUNT + 1))
        if [ ${RETRY_COUNT} -ge ${MAX_RETRIES} ]; then
            echo "Error: Keycloak is not healthy after ${MAX_RETRIES} retries."
            exit 1
        fi
        echo "Waiting for Keycloak to be healthy... ${RETRY_COUNT}/${MAX_RETRIES}"
        sleep 5
    fi
done

chmod +x ./create-keycloak-data.sh

# Configure Keycloak
./create-keycloak-data.sh
if [ $? -ne 0 ]; then
    echo "Error: Keycloak configuration failed"
    exit 1
fi