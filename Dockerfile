FROM amazoncorretto:21-alpine
WORKDIR /app
ENV RUN_ARGS ${RUN_ARGS}
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src ./src
RUN ./mvnw clean package
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "-Djava.security.egd=file:/dev/./urandom"]