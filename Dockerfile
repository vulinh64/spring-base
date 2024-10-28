FROM eclipse-temurin:21-jdk AS builder
WORKDIR /builder
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src
RUN ./mvnw clean package && java -Djarmode=tools -jar target/app.jar extract --layers --destination extracted

FROM eclipse-temurin:21-jre
WORKDIR /application

RUN groupadd spring && useradd spring -g spring

USER spring:spring

COPY --from=builder --chown=spring:spring /builder/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /builder/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-XX:+UseG1GC", "-jar", "app.jar"]

