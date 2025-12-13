@echo off

IF EXIST .\build\spring-base-commons rmdir /s /q .\build\spring-base-commons

:: Shallowly clone the repository
git clone --depth 1 https://github.com/vulinh64/spring-base-commons.git .\build\spring-base-commons

rmdir /s /q .\build\spring-base-commons\.git

call .\build\spring-base-commons\mvnw.cmd clean install -f .\build\spring-base-commons\pom.xml
