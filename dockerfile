FROM openjdk:17-jdk-alpine
EXPOSE 8080
COPY target/spring-base-1.0.0.jar spring-base-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/spring-base-1.0.0.jar"]