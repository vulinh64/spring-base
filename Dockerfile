FROM amazoncorretto:21-alpine
WORKDIR /app
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src
RUN ./mvnw clean package
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/app.jar", "-Djava.security.egd=file:/dev/./urandom"]
