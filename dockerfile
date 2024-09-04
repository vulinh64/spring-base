FROM amazoncorretto:21-alpine-jdk
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/spring-base-1.0.3.jar spring-base.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar spring-base.jar -XX:+UseZGC -XX:+ZGenerational
