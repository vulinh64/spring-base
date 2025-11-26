FROM quay.io/keycloak/keycloak:26.4

# Copy the custom HealthCheck.java file into the Keycloak image
COPY HealthCheck.java /opt/keycloak/HealthCheck.java
WORKDIR /opt/keycloak

CMD ["start-dev"]