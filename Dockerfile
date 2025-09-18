#
# Multi-stage Dockerfile for Java Spring Boot application
#

# Stage 1: Build stage - Compile the application and create a minimal JRE
FROM amazoncorretto:25-alpine-full AS build
WORKDIR /usr/src/project

ENV JAVA_VERSION=25

# Copy Maven configuration files first to leverage Docker cache
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Download dependencies (will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src/ src/

# Build the application using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Extract the JAR to analyze its dependencies
RUN jar xf target/app.jar

# Use jdeps to identify necessary Java modules for a minimal JRE
RUN jdeps  \
    --ignore-missing-deps  \
    -q --recursive  \
    --multi-release ${JAVA_VERSION} \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*' \
    target/app.jar > deps.info

# Create a custom JRE with only the required modules
# jdk.crypto.ec is needed for HTTPS
# JDEPS currently does not detect this module
RUN jlink \
    --add-modules $(cat deps.info),jdk.crypto.ec \
    --strip-java-debug-attributes  \
    --compress 2  \
    --no-header-files  \
    --no-man-pages \
    --output /jre-minimalist

# Stage 2: Production stage - Minimal Alpine image with custom JRE
FROM alpine:3.22 AS final

# Set up Java environment
ENV JAVA_HOME=/opt/java/jre-minimalist
ENV PATH=$JAVA_HOME/bin:$PATH

# Copy the custom JRE from the build stage
COPY --from=build /jre-minimalist $JAVA_HOME

# Create a non-root user for security
RUN addgroup -S springgroup \
    && adduser -S springuser -G springgroup \
    && mkdir -p /app \
    && chown -R springuser:springgroup /app

# Copy application artifacts from build stage
COPY --from=build /usr/src/project/target/app.jar /app/

WORKDIR /app

USER springuser

#
# Run the application with optimized JVM settings
#

# - UseCompactObjectHeaders: See JEP 519 (https://openjdk.org/jeps/519)
# - MaxRAMPercentage: Limit max heap to 75% of container memory
# - InitialRAMPercentage: Start with 50% of container memory
# - MaxMetaspaceSize: Limit metaspace to 512MB
# - UseG1GC: Use the G1 garbage collector for better performance
ENTRYPOINT ["java", "-XX:+UseCompactObjectHeaders", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-XX:MaxMetaspaceSize=512m", "-XX:+UseG1GC", "-jar", "app.jar"]