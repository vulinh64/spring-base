@echo off

SET COMMONS_NAME=spring-base-commons
SET COMMONS_GROUP_ID=com.vulinh
SET COMMONS_VERSION=3.0.0
SET GITHUB_USER=vulinh64

SET JAR_FILE=%COMMONS_NAME%-%COMMONS_VERSION%.jar
SET SOURCES_FILE=%COMMONS_NAME%-%COMMONS_VERSION%-sources.jar
SET BASE_URL=https://github.com/%GITHUB_USER%/%COMMONS_NAME%/releases/download/%COMMONS_VERSION%

:: Create build directory if it doesn't exist
IF NOT EXIST .\build mkdir .\build

:: Download the JAR file
echo Downloading %JAR_FILE%...
curl -L -o .\build\%JAR_FILE% %BASE_URL%/%JAR_FILE%

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to download JAR file
    exit /b 1
)

:: Download the sources JAR file (optional, failure is non-fatal)
echo Downloading %SOURCES_FILE%...
SET SOURCES_DOWNLOADED=0
curl -L -o .\build\%SOURCES_FILE% %BASE_URL%/%SOURCES_FILE%
IF %ERRORLEVEL% EQU 0 (
    SET SOURCES_DOWNLOADED=1
)

:: Clean the target folder in local .m2 repository if it exists
SET M2_PATH=%USERPROFILE%\.m2\repository\%COMMONS_GROUP_ID:.=\%\%COMMONS_NAME%\%COMMONS_VERSION%

IF EXIST "%M2_PATH%" (
    echo Cleaning existing Maven repository folder...
    rmdir /s /q "%M2_PATH%"
)

:: Install the JAR to local Maven repository
echo Installing %JAR_FILE% to local Maven repository...
call mvnw.cmd install:install-file ^
    -Dfile=.\build\%JAR_FILE% ^
    -DgroupId=%COMMONS_GROUP_ID% ^
    -DartifactId=%COMMONS_NAME% ^
    -Dversion=%COMMONS_VERSION% ^
    -Dpackaging=jar

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to install JAR file
    exit /b 1
)

:: Install the sources JAR only if it was downloaded successfully
IF %SOURCES_DOWNLOADED% EQU 1 (
    echo Installing %SOURCES_FILE% to local Maven repository...
    call mvnw.cmd install:install-file ^
        -Dfile=.\build\%SOURCES_FILE% ^
        -DgroupId=%COMMONS_GROUP_ID% ^
        -DartifactId=%COMMONS_NAME% ^
        -Dversion=%COMMONS_VERSION% ^
        -Dpackaging=jar ^
        -Dclassifier=sources

    IF %ERRORLEVEL% NEQ 0 (
        echo Failed to install sources JAR file
        exit /b 1
    )
)

echo Successfully installed %COMMONS_NAME% version %COMMONS_VERSION%