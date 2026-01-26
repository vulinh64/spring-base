@echo off

SET SPRING_BASE_COMMONS_VERSION=2.4.3

IF EXIST .\build\spring-base-commons rmdir /s /q .\build\spring-base-commons

:: Shallowly clone the repository
git clone --depth 1 --branch %SPRING_BASE_COMMONS_VERSION% https://github.com/vulinh64/spring-base-commons.git .\build\spring-base-commons

rmdir /s /q .\build\spring-base-commons\.git

call .\build\spring-base-commons\mvnw.cmd clean install -f .\build\spring-base-commons\pom.xml
