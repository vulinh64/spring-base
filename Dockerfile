#
# Multi-stage Dockerfile for Java Spring Boot application
#

# Stage 1: Build stage
# Install the application and its dependencies
# Create a custom minimal JRE
FROM maven:3.9.14-amazoncorretto-25-alpine AS build

WORKDIR /usr/src/project

ENV JAVA_VERSION=25
ENV APP_NAME=app.jar
ENV DEPS_FILE=deps.info

ENV PARENT_NAME=spring-base-parent
ENV COMMONS_NAME=spring-base-commons
ENV COMMONS_GROUP_ID=com.vulinh
ENV GITHUB_USER=vulinh64

# Copy the main Maven configuration first for dependency-layer caching
COPY pom.xml ./

# Download and install the custom parent POM
RUN echo "Reading ${PARENT_NAME} version from pom.xml..." \
    && PARENT_VERSION="$(sed -n \
        '/<parent>/,/<\/parent>/s:.*<version>\([^<]*\)</version>.*:\1:p' \
        pom.xml | sed -n '1p')" \
    && if [ -z "${PARENT_VERSION}" ]; then \
        echo "Failed to read ${PARENT_NAME} version from pom.xml"; \
        exit 1; \
    else \
        echo "Found ${PARENT_NAME} version ${PARENT_VERSION}"; \
        PARENT_POM="${PARENT_NAME}-${PARENT_VERSION}.pom"; \
        PARENT_DIR="${HOME}/.m2/repository/com/vulinh/${PARENT_NAME}/${PARENT_VERSION}"; \
        PARENT_URL="https://github.com/${GITHUB_USER}/${PARENT_NAME}/releases/download/${PARENT_VERSION}/${PARENT_POM}"; \
        echo "Downloading ${PARENT_POM}..."; \
        if ! wget -O "${PARENT_POM}" "${PARENT_URL}"; then \
            echo "Failed to download ${PARENT_POM}"; \
            exit 1; \
        fi; \
        echo "Installing ${PARENT_POM} into the local Maven repository..."; \
        mkdir -p "${PARENT_DIR}"; \
        cp "${PARENT_POM}" "${PARENT_DIR}/${PARENT_POM}"; \
        rm -f "${PARENT_DIR}"/*.lastUpdated; \
        rm -f "${PARENT_POM}"; \
        echo "Successfully installed ${PARENT_NAME} version ${PARENT_VERSION}"; \
    fi

# Read the commons version inherited from the parent POM
RUN echo "Reading ${COMMONS_NAME} version from pom.xml \${spring-base-commons.version}..." \
    && mvn help:evaluate \
        -Dexpression="spring-base-commons.version" \
        -q \
        -DforceStdout \
        2>/dev/null \
        | sed -n '/^[0-9][0-9A-Za-z_.-]*$/ { p; q; }' \
        > commons-version.txt \
    && COMMONS_VERSION="$(cat commons-version.txt)" \
    && if [ -z "${COMMONS_VERSION}" ]; then \
        echo "Failed to evaluate spring-base-commons.version from pom.xml"; \
        exit 1; \
    else \
        echo "Found ${COMMONS_NAME} version ${COMMONS_VERSION}"; \
    fi

# Download the pre-built commons JAR from GitHub Releases
RUN COMMONS_VERSION="$(cat commons-version.txt)" \
    && COMMONS_JAR="${COMMONS_NAME}-${COMMONS_VERSION}.jar" \
    && COMMONS_URL="https://github.com/${GITHUB_USER}/${COMMONS_NAME}/releases/download/${COMMONS_VERSION}/${COMMONS_JAR}" \
    && echo "Downloading ${COMMONS_JAR}..." \
    && if ! wget -O "${COMMONS_NAME}.jar" "${COMMONS_URL}"; then \
        echo "Failed to download ${COMMONS_JAR}"; \
        exit 1; \
    fi

# Install the commons JAR into the local Maven repository
RUN COMMONS_VERSION="$(cat commons-version.txt)" \
    && echo "Installing ${COMMONS_NAME} version ${COMMONS_VERSION}..." \
    && mvn install:install-file \
        -Dfile="${COMMONS_NAME}.jar" \
        -DgroupId="${COMMONS_GROUP_ID}" \
        -DartifactId="${COMMONS_NAME}" \
        -Dversion="${COMMONS_VERSION}" \
        -Dpackaging=jar \
    && echo "Successfully installed ${COMMONS_NAME} version ${COMMONS_VERSION}"

# Copy source code
COPY src/ src/

# Build the application using Maven
RUN mvn clean package -DskipTests

# Extract the executable Spring Boot JAR to analyze its dependencies
RUN jar xf "target/${APP_NAME}"

# Use jdeps to identify the Java modules required by the application
RUN jdeps \
    --ignore-missing-deps \
    -q \
    --recursive \
    --multi-release "${JAVA_VERSION}" \
    --print-module-deps \
    --class-path "BOOT-INF/lib/*" \
    "target/${APP_NAME}" \
    > "${DEPS_FILE}"

# Create a custom JRE containing only the required modules
# jdk.crypto.ec is required for HTTPS but may not be detected by jdeps
RUN jlink \
    --add-modules "$(cat "${DEPS_FILE}"),jdk.crypto.ec" \
    --strip-java-debug-attributes \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /jre-minimalist

# Stage 2: Production stage
FROM alpine:3.23.3 AS final

ENV JAVA_HOME=/opt/java/jre-minimalist
ENV PATH="${JAVA_HOME}/bin:${JAVA_HOME}/lib:${PATH}"

ENV USER=springuser
ENV GROUP=springgroup
ENV WORKDIR=app
ENV APP_NAME=app.jar

# Copy the custom JRE from the build stage
COPY --from=build /jre-minimalist "${JAVA_HOME}"

# Create a non-root user and application directory
RUN addgroup -S "${GROUP}" \
    && adduser -S "${USER}" -G "${GROUP}" \
    && mkdir -p "/${WORKDIR}" \
    && chown -R "${USER}:${GROUP}" "/${WORKDIR}"

# Copy the application JAR from the build stage
COPY --from=build \
    "/usr/src/project/target/${APP_NAME}" \
    "/${WORKDIR}/${APP_NAME}"

WORKDIR "/${WORKDIR}"

USER "${USER}"

#
# Run the application with container-aware JVM settings
#

# UseCompactObjectHeaders: See JEP 519
# MaxRAMPercentage: Limit maximum heap to 75% of container memory
# InitialRAMPercentage: Start with 50% of container memory
# MaxMetaspaceSize: Limit metaspace to 512 MB
ENTRYPOINT ["java", \
    "-XX:+UseCompactObjectHeaders", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:MaxMetaspaceSize=512m", \
    "-jar", \
    "app.jar"]