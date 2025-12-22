#
# Multi-stage Dockerfile for Java Spring Boot application
#

# Stage 1: Build stage
# Install the application and its dependencies
# Create a custom minimal JRE
FROM amazoncorretto:25-alpine-full AS build

# Install git
RUN apk add --no-cache git

WORKDIR /usr/src/project

ENV JAVA_VERSION=25
ENV APP_NAME=app.jar
ENV DEPS_FILE=deps.info

# Change this when there is an update
ENV SPRING_BASE_COMMONS_VERSION=2.2.0

# Clone the spring-base-commons repository
RUN git clone --depth 1 --branch ${SPRING_BASE_COMMONS_VERSION} https://github.com/vulinh64/spring-base-commons.git

# Copy Maven wrapper files
COPY mvnw ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# Build and install spring-base-commons to local Maven repository
RUN ./mvnw clean install -f spring-base-commons/pom.xml -DskipTests

# Copy main application Maven configuration files
COPY pom.xml ./

# Copy source code
COPY src/ src/

# Build the application using Maven wrapper
RUN ./mvnw clean package -DskipTests

# Extract the JAR to analyze its dependencies
RUN jar xf target/${APP_NAME}

# Use jdeps to identify necessary Java modules for a minimal JRE
RUN jdeps  \
    --ignore-missing-deps  \
    -q --recursive  \
    --multi-release ${JAVA_VERSION} \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*' \
    target/${APP_NAME} > ${DEPS_FILE}

# Create a custom JRE with only the required modules
# jdk.crypto.ec is needed for HTTPS
# JDEPS currently does not detect this module
RUN jlink \
    --add-modules $(cat ${DEPS_FILE}) \
    --strip-java-debug-attributes  \
    --compress 2  \
    --no-header-files  \
    --no-man-pages \
    --output /jre-minimalist

# Stage 2: Production stage - Minimal Alpine image with custom JRE
FROM alpine:3.22 AS final

ENV JAVA_HOME=/opt/java/jre-minimalist
ENV PATH=$JAVA_HOME/bin:$JAVA_HOME/lib:$PATH
ENV USER=springuser
ENV GROUP=springgroup
ENV WORKDIR=app
ENV APP_NAME=app.jar

# Copy the custom JRE from the build stage
COPY --from=build /jre-minimalist $JAVA_HOME

# Create a non-root user for security
RUN addgroup -S ${GROUP} \
    && adduser -S ${USER} -G ${GROUP} \
    && mkdir -p /app \
    && chown -R ${USER}:${GROUP} /${WORKDIR}

# Copy application artifacts from build stage
COPY --from=build /usr/src/project/target/${APP_NAME} /${WORKDIR}/

WORKDIR /${WORKDIR}

USER ${USER}

#
# Run the application with optimized JVM settings
#

# - UseCompactObjectHeaders: See JEP 519 (https://openjdk.org/jeps/519)
# - MaxRAMPercentage: Limit max heap to 75% of container memory
# - InitialRAMPercentage: Start with 50% of container memory
# - MaxMetaspaceSize: Limit metaspace to 512MB
ENTRYPOINT ["java", \
    "-XX:+UseCompactObjectHeaders", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:MaxMetaspaceSize=512m", \
    "-jar", \
    "app.jar"]