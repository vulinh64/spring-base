#!/usr/bin/env sh

set -e

GITHUB_USER=vulinh64
GROUP_ID=com.vulinh
PARENT_ARTIFACT_ID=spring-base-parent
COMMONS_ARTIFACT_ID=spring-base-commons
BUILD_DIR=./build

# Ensure mvnw is executable
chmod +x ./mvnw

if [ -z "${JAVA_HOME}" ]; then
    JAVA_BIN=$(command -v java || true)

    if [ -n "${JAVA_BIN}" ]; then
        if command -v readlink >/dev/null 2>&1; then
            JAVA_BIN=$(readlink -f "${JAVA_BIN}" 2>/dev/null || printf '%s' "${JAVA_BIN}")
        fi

        JAVA_HOME=$(dirname "$(dirname "${JAVA_BIN}")")
        export JAVA_HOME
    fi
fi

echo "Reading parent POM version from pom.xml..."
PARENT_VERSION=$(sed -n '/<parent>/,/<\/parent>/s:.*<version>\([^<]*\)</version>.*:\1:p' pom.xml | sed -n '1p')

if [ -z "${PARENT_VERSION}" ]; then
    echo "Failed to evaluate parent version from pom.xml"
    exit 1
fi

PARENT_POM_FILE="${PARENT_ARTIFACT_ID}-${PARENT_VERSION}.pom"
PARENT_BASE_URL="https://github.com/${GITHUB_USER}/${PARENT_ARTIFACT_ID}/releases/download/${PARENT_VERSION}/${PARENT_POM_FILE}"
GROUP_PATH=$(printf '%s' "${GROUP_ID}" | tr '.' '/')
PARENT_M2_PATH="${HOME}/.m2/repository/${GROUP_PATH}/${PARENT_ARTIFACT_ID}/${PARENT_VERSION}"

# Create build directory if it doesn't exist
mkdir -p "${BUILD_DIR}"

echo "Downloading parent POM"
curl -fL -o "${BUILD_DIR}/${PARENT_POM_FILE}" "${PARENT_BASE_URL}"

echo "Installing ${PARENT_POM_FILE} to local Maven repository..."
mkdir -p "${PARENT_M2_PATH}"
cp "${BUILD_DIR}/${PARENT_POM_FILE}" "${PARENT_M2_PATH}/${PARENT_POM_FILE}"
rm -f "${PARENT_M2_PATH}"/*.lastUpdated

echo "Reading ${COMMONS_ARTIFACT_ID} version from pom.xml \${spring-base-commons.version}..."
COMMONS_VERSION=$(./mvnw help:evaluate -Dexpression="spring-base-commons.version" -q -DforceStdout 2>/dev/null | sed -n '/^[0-9][0-9A-Za-z_.-]*$/ { p; q; }')

if [ -z "${COMMONS_VERSION}" ]; then
    echo "Failed to evaluate spring-base-commons.version from pom.xml"
    exit 1
fi

JAR_FILE="${COMMONS_ARTIFACT_ID}-${COMMONS_VERSION}.jar"
SOURCES_FILE="${COMMONS_ARTIFACT_ID}-${COMMONS_VERSION}-sources.jar"
BASE_URL="https://github.com/${GITHUB_USER}/${COMMONS_ARTIFACT_ID}/releases/download/${COMMONS_VERSION}"

# Download the JAR file
echo "Downloading ${JAR_FILE}..."
curl -fL -o "${BUILD_DIR}/${JAR_FILE}" "${BASE_URL}/${JAR_FILE}"

if [ $? -ne 0 ]; then
    echo "Failed to download JAR file"
    exit 1
fi

# Download the sources JAR file (optional, failure is non-fatal)
echo "Downloading ${SOURCES_FILE}..."
SOURCES_DOWNLOADED=0
if curl -fL -o "${BUILD_DIR}/${SOURCES_FILE}" "${BASE_URL}/${SOURCES_FILE}"; then
    SOURCES_DOWNLOADED=1
fi

# Clean the target folder in local .m2 repository if it exists
M2_PATH="${HOME}/.m2/repository/${GROUP_PATH}/${COMMONS_ARTIFACT_ID}/${COMMONS_VERSION}"

if [ -d "${M2_PATH}" ]; then
    echo "Cleaning existing Maven repository folder..."
    rm -rf "${M2_PATH}"
fi

# Install the JAR to local Maven repository
echo "Installing ${JAR_FILE} to local Maven repository..."
./mvnw install:install-file \
    -Dfile="${BUILD_DIR}/${JAR_FILE}" \
    -DgroupId="${GROUP_ID}" \
    -DartifactId="${COMMONS_ARTIFACT_ID}" \
    -Dversion="${COMMONS_VERSION}" \
    -Dpackaging=jar

if [ $? -ne 0 ]; then
    echo "Failed to install JAR file"
    exit 1
fi

if [ "${SOURCES_DOWNLOADED}" = "1" ]; then
    echo "Installing ${SOURCES_FILE} to local Maven repository..."
    ./mvnw install:install-file \
        -Dfile="${BUILD_DIR}/${SOURCES_FILE}" \
        -DgroupId="${GROUP_ID}" \
        -DartifactId="${COMMONS_ARTIFACT_ID}" \
        -Dversion="${COMMONS_VERSION}" \
        -Dpackaging=jar \
        -Dclassifier=sources

    if [ $? -ne 0 ]; then
        echo "Failed to install sources JAR file"
        exit 1
    fi
fi

echo "Successfully installed ${COMMONS_ARTIFACT_ID} version ${COMMONS_VERSION}"
