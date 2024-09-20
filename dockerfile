FROM amazoncorretto:21-alpine
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH=$PATH:$JAVA_HOME
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .
COPY src/ src/
RUN chmod +x mvnw
RUN ./mvnw clean install
COPY target/*.jar app.jar
CMD ["java", "-ea", "-jar", "app.jar", "-Djava.security.egd=file:/dev/./urandom"]