package com.vulinh.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.testcontainers.containers.wait.strategy.ShellStrategy;

@RequiredArgsConstructor
public enum HealthCheckCommand {
  POSTGRESQL("pg_isready -U %s"),
  REDIS("redis-cli -a %s ping");

  final String shellCommand;

  public ShellStrategy shellStrategyHealthCheck(Object... args) {
    return new ShellStrategy()
        .withCommand(ArrayUtils.isEmpty(args) ? shellCommand : shellCommand.formatted(args));
  }
}
