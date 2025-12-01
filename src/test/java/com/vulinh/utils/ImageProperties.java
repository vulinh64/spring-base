package com.vulinh.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.testcontainers.containers.wait.strategy.ShellStrategy;

@RequiredArgsConstructor
public enum ImageProperties {
  POSTGRESQL("postgres", "18.1-alpine3.22", "pg_isready -U %s"),
  // Weird health check, but it is because there is no way to do Keycloak healthcheck
  // Because Keycloak image is based on alpine
  KEYCLOAK(
      "quay.io/keycloak/keycloak",
      "26.4.6",
      """
      [ -f /tmp/HealthCheck.java ] || echo "public class HealthCheck { public static void main(String[] args) throws java.lang.Throwable { java.net.URI uri = java.net.URI.create(args[0]); System.exit(java.net.HttpURLConnection.HTTP_OK == ((java.net.HttpURLConnection)uri.toURL().openConnection()).getResponseCode() ? 0 : 1); } }" > /tmp/HealthCheck.java && java /tmp/HealthCheck.java http://localhost:9000/health/live
      """),
  // Health check cannot be used during test phase for whatever reasons
  // So don't use the shell strategy for RabbitMQ
  RABBIT_MQ(
      "rabbitmq", "4.2.1-management", "/opt/rabbitmq/escript/rabbitmq-diagnostics ping --quiet");

  final String imageName;
  final String tag;
  final String shellCommand;

  public String getFullImageName() {
    return "%s:%s".formatted(imageName, tag);
  }

  public ShellStrategy shellStrategyHealthCheck(Object... args) {
    return new ShellStrategy()
        .withCommand(ArrayUtils.isEmpty(args) ? shellCommand : shellCommand.formatted(args));
  }
}
