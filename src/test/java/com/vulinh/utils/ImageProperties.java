package com.vulinh.utils;

import module java.base;

import com.vulinh.utils.healthcheck.PromptMessageShellHealthCheckDelegate;
import com.vulinh.utils.healthcheck.RabbitMQDelayHealthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.wait.strategy.ShellStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

@Slf4j
@RequiredArgsConstructor
public enum ImageProperties {
  POSTGRESQL(
      "postgres",
      "18.1-alpine3.22",
      args ->
          PromptMessageShellHealthCheckDelegate.builder()
              .containerDescription("PostgreSQL")
              .actualWaitStrategy(
                  new ShellStrategy().withCommand("pg_isready -U %s".formatted(args)))
              .build()),
  // Weird health check, but it is because there is no way to do Keycloak healthcheck
  // Because Keycloak image is based on alpine
  KEYCLOAK(
      "quay.io/keycloak/keycloak",
      "26.4.6",
      _ ->
          PromptMessageShellHealthCheckDelegate.builder()
              .containerDescription("Keycloak")
              .actualWaitStrategy(
                  new ShellStrategy()
                      .withCommand(
                          """
                          [ -f /tmp/HealthCheck.java ] || echo "public class HealthCheck { public static void main(String[] args) throws java.lang.Throwable { java.net.URI uri = java.net.URI.create(args[0]); System.exit(java.net.HttpURLConnection.HTTP_OK == ((java.net.HttpURLConnection)uri.toURL().openConnection()).getResponseCode() ? 0 : 1); } }" > /tmp/HealthCheck.java && java /tmp/HealthCheck.java http://localhost:9000/health/live
                          """))
              .build()),
  // Use alpine image to save space
  // The management-alpine is for local development debugging
  RABBIT_MQ(
      "rabbitmq",
      "4.2.1-alpine",
      _ ->
          PromptMessageShellHealthCheckDelegate.builder()
              .containerDescription("RabbitMQ")
              .actualWaitStrategy(new RabbitMQDelayHealthCheck())
              .build());

  final String imageName;
  final String tag;
  final Function<Object[], ? extends WaitStrategy> waitStrategy;

  public String getFullImageName() {
    return "%s:%s".formatted(imageName, tag);
  }

  public WaitStrategy shellStrategyHealthCheck(Object... args) {
    return waitStrategy.apply(args);
  }
}
