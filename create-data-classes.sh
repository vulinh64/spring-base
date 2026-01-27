#!/usr/bin/env sh

set -e

SPRING_BASE_COMMONS_VERSION=2.4.4

if [ -d "./build/spring-base-commons" ]; then
  rm -rf ./build/spring-base-commons
fi

# Shallowly clone the repository at the specified version
git clone --depth 1 --branch "$SPRING_BASE_COMMONS_VERSION" https://github.com/vulinh64/spring-base-commons.git ./build/spring-base-commons

rm -rf ./build/spring-base-commons/.git

chmod +x ./build/spring-base-commons/mvnw

./build/spring-base-commons/mvnw clean install -f ./build/spring-base-commons/pom.xml