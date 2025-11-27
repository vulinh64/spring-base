#!/bin/bash

# Check if Docker daemon is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker daemon is not running."
    echo "Please start Docker service and run this script again."
    read -p "Press any key to continue..."
    exit 1
fi

docker compose down
docker rmi --force spring-base:1.0.0
docker compose up --detach

KEYCLOAK_CONTAINER="standalone-keycloak"
KCADM_PATH="/opt/keycloak/bin/kcadm.sh"
KEYCLOAK_REALM="spring-base"
CLIENT_ID="spring-base-client"
KEYCLOAK_OVERLORD="admin"
KEYCLOAK_ADMIN_PASSWORD="123456"

echo "Configuring KCADM credentials..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" config credentials --server http://localhost:8080 --realm master --user "$KEYCLOAK_OVERLORD" --password "$KEYCLOAK_ADMIN_PASSWORD"

docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create realms -s realm="$KEYCLOAK_REALM" -s enabled=true

echo "Creating client [$CLIENT_ID]..."

CLIENT_UUID=$(docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create clients -r "$KEYCLOAK_REALM" -s clientId="$CLIENT_ID" -s enabled=true -s publicClient=true -s directAccessGrantsEnabled=true -i)

# --- Role and User Setup ---
ROLE_ADMIN="ADMIN"
ROLE_POWER_USER="POWER_USER"
ROLE_USER="USER"

ADMIN_USERNAME="administrator"
POWER_USER_USERNAME="power_user"
USER_USERNAME="user"

docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create clients/"$CLIENT_UUID"/roles -r "$KEYCLOAK_REALM" -s name="$ROLE_ADMIN"
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create clients/"$CLIENT_UUID"/roles -r "$KEYCLOAK_REALM" -s name="$ROLE_POWER_USER"
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create clients/"$CLIENT_UUID"/roles -r "$KEYCLOAK_REALM" -s name="$ROLE_USER"

echo "Creating admin user [$ADMIN_USERNAME]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create users -r "$KEYCLOAK_REALM" -s username="$ADMIN_USERNAME" -s enabled=true -s email=administrator@email.com -s firstName=Administrator -s lastName=User
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" set-password -r "$KEYCLOAK_REALM" --username "$ADMIN_USERNAME" --new-password 123456

echo "Assigning client role [$ROLE_ADMIN] to user [$ADMIN_USERNAME] in client [$CLIENT_ID]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" add-roles -r "$KEYCLOAK_REALM" --uusername "$ADMIN_USERNAME" --cclientid "$CLIENT_ID" --rolename "$ROLE_ADMIN"
echo "Created user [$ADMIN_USERNAME] with password 123456 and client role [$ROLE_ADMIN] in client [$CLIENT_ID]"

echo "Creating regular user [$USER_USERNAME]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create users -r "$KEYCLOAK_REALM" -s username="$USER_USERNAME" -s enabled=true -s email=user@email.com -s firstName=Normal -s lastName=User
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" set-password -r "$KEYCLOAK_REALM" --username "$USER_USERNAME" --new-password 123456

echo "Assigning client role [$ROLE_USER] to user [$USER_USERNAME] in client [$CLIENT_ID]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" add-roles -r "$KEYCLOAK_REALM" --uusername "$USER_USERNAME" --cclientid "$CLIENT_ID" --rolename "$ROLE_USER"
echo "Created user [$USER_USERNAME] with password 123456 and client role [$ROLE_USER] in client [$CLIENT_ID]"

echo "Creating power user [$POWER_USER_USERNAME]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" create users -r "$KEYCLOAK_REALM" -s username="$POWER_USER_USERNAME" -s enabled=true -s email=power_user@email.com -s firstName=Power -s lastName=User
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" set-password -r "$KEYCLOAK_REALM" --username "$POWER_USER_USERNAME" --new-password 123456

echo "Assigning client role [$ROLE_POWER_USER] to user [$POWER_USER_USERNAME] in client [$CLIENT_ID]..."
docker exec "$KEYCLOAK_CONTAINER" "$KCADM_PATH" add-roles -r "$KEYCLOAK_REALM" --uusername "$POWER_USER_USERNAME" --cclientid "$CLIENT_ID" --rolename "$ROLE_POWER_USER"
echo "Created user [$POWER_USER_USERNAME] with password 123456 and client role [$ROLE_POWER_USER] in client [$CLIENT_ID]"