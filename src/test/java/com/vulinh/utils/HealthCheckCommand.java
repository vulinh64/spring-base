package com.vulinh.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.testcontainers.containers.wait.strategy.ShellStrategy;

@RequiredArgsConstructor
public enum HealthCheckCommand {
  POSTGRESQL("pg_isready -U %s"),
  REDIS("redis-cli -a %s ping"),
  // Weird health check, but it is because there is no way to do Keycloak healthcheck
  // Because Keycloak image is based on alpine
  KEYCLOAK(
      """
      [ -f /tmp/HealthCheck.java ] || echo "public class HealthCheck { public static void main(String[] args) throws java.lang.Throwable { java.net.URI uri = java.net.URI.create(args[0]); System.exit(java.net.HttpURLConnection.HTTP_OK == ((java.net.HttpURLConnection)uri.toURL().openConnection()).getResponseCode() ? 0 : 1); } }" > /tmp/HealthCheck.java && java /tmp/HealthCheck.java http://localhost:9000/health/live
      """);

  final String shellCommand;

  public ShellStrategy shellStrategyHealthCheck(Object... args) {
    return new ShellStrategy()
        .withCommand(ArrayUtils.isEmpty(args) ? shellCommand : shellCommand.formatted(args));
  }
}
