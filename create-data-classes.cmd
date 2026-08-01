@echo off
SETLOCAL EnableDelayedExpansion

SET GITHUB_USER=vulinh64
SET GROUP_ID=com.vulinh
SET PARENT_ARTIFACT_ID=spring-base-parent
SET COMMONS_ARTIFACT_ID=spring-base-commons
SET BUILD_DIR=.\build

echo Reading parent POM version from pom.xml...

FOR /F "usebackq delims=" %%V in (`powershell -NoProfile -Command "$pom = [xml](Get-Content -Raw -LiteralPath 'pom.xml'); $pom.project.parent.version"`) DO (
    IF "!PARENT_VERSION!"=="" SET "PARENT_VERSION=%%V"
)

IF "!PARENT_VERSION!"=="" (
    echo Failed to evaluate parent version from pom.xml
    exit /b 1
)

SET PARENT_POM_FILE=%PARENT_ARTIFACT_ID%-%PARENT_VERSION%.pom
SET PARENT_BASE_URL=https://github.com/%GITHUB_USER%/%PARENT_ARTIFACT_ID%/releases/download/%PARENT_VERSION%/%PARENT_POM_FILE%
SET PARENT_M2_PATH=%USERPROFILE%\.m2\repository\%GROUP_ID:.=\%\%PARENT_ARTIFACT_ID%\%PARENT_VERSION%

:: Create build directory if it doesn't exist
IF NOT EXIST %BUILD_DIR% mkdir %BUILD_DIR%

echo Downloading parent POM
curl -fL -o %BUILD_DIR%\%PARENT_POM_FILE% %PARENT_BASE_URL%

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to download parent POM from %PARENT_BASE_URL%
    exit /b 1
)

echo Installing %PARENT_POM_FILE% to local Maven repository...
IF NOT EXIST "%PARENT_M2_PATH%" mkdir "%PARENT_M2_PATH%"
copy /Y "%BUILD_DIR%\%PARENT_POM_FILE%" "%PARENT_M2_PATH%\%PARENT_POM_FILE%" >nul

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to install parent POM file
    exit /b 1
)

del /q "%PARENT_M2_PATH%\*.lastUpdated" 2>nul

echo Reading %COMMONS_ARTIFACT_ID% version from pom.xml ${spring-base-commons.version}...
FOR /F "usebackq delims=" %%V IN (`call .\mvnw.cmd help:evaluate -Dexpression^="spring-base-commons.version" -q -DforceStdout 2^>nul ^| findstr /R "^[0-9][0-9A-Za-z_.-]*$" ^& exit /b 0`) DO (
    IF "!COMMONS_VERSION!"=="" SET "COMMONS_VERSION=%%V"
)

IF "!COMMONS_VERSION!"=="" (
    echo Failed to evaluate spring-base-commons.version from pom.xml
    exit /b 1
)

SET JAR_FILE=%COMMONS_ARTIFACT_ID%-!COMMONS_VERSION!.jar
SET SOURCES_FILE=%COMMONS_ARTIFACT_ID%-!COMMONS_VERSION!-sources.jar
SET BASE_URL=https://github.com/%GITHUB_USER%/%COMMONS_ARTIFACT_ID%/releases/download/!COMMONS_VERSION!

:: Download the JAR file
echo Downloading %JAR_FILE%...
curl -fL -o %BUILD_DIR%\%JAR_FILE% %BASE_URL%/%JAR_FILE%

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to download JAR file
    exit /b 1
)

:: Download the sources JAR file (optional, failure is non-fatal)
echo Downloading %SOURCES_FILE%...
SET SOURCES_DOWNLOADED=0
curl -fL -o %BUILD_DIR%\%SOURCES_FILE% %BASE_URL%/%SOURCES_FILE%
IF %ERRORLEVEL% EQU 0 (
    SET SOURCES_DOWNLOADED=1
)

:: Clean the target folder in local .m2 repository if it exists
SET M2_PATH=%USERPROFILE%\.m2\repository\%GROUP_ID:.=\%\%COMMONS_ARTIFACT_ID%\%COMMONS_VERSION%

IF EXIST "%M2_PATH%" (
    echo Cleaning existing Maven repository folder...
    rmdir /s /q "%M2_PATH%"
)

:: Install the JAR to local Maven repository
echo Installing %JAR_FILE% to local Maven repository...
call mvnw.cmd install:install-file ^
    -Dfile=%BUILD_DIR%\%JAR_FILE% ^
    -DgroupId=%GROUP_ID% ^
    -DartifactId=%COMMONS_ARTIFACT_ID% ^
    -Dversion=%COMMONS_VERSION% ^
    -Dpackaging=jar

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to install JAR file
    exit /b 1
)

:: Install the sources JAR only if it was downloaded successfully
IF NOT "%SOURCES_DOWNLOADED%"=="1" GOTO SKIP_SOURCES_INSTALL

echo Installing %SOURCES_FILE% to local Maven repository...
call mvnw.cmd install:install-file ^
    -Dfile=%BUILD_DIR%\%SOURCES_FILE% ^
    -DgroupId=%GROUP_ID% ^
    -DartifactId=%COMMONS_ARTIFACT_ID% ^
    -Dversion=%COMMONS_VERSION% ^
    -Dpackaging=jar ^
    -Dclassifier=sources

IF %ERRORLEVEL% NEQ 0 (
    echo Failed to install sources JAR file
    exit /b 1
)

:SKIP_SOURCES_INSTALL
echo Successfully installed %COMMONS_ARTIFACT_ID% version %COMMONS_VERSION%
