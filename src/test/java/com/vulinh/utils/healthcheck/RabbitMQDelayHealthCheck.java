package com.vulinh.utils.healthcheck;

import module java.base;

import org.awaitility.Awaitility;
import org.testcontainers.containers.wait.strategy.ShellStrategy;

public class RabbitMQDelayHealthCheck extends ShellStrategy {

  static final String[] COMMAND = "rabbitmq-diagnostics -q status".split("\\s+");

  static final long DEFAULT_POLL_DELAY = 5;
  static final long DEFAULT_TIMEOUT = 60;

  @Override
  protected void waitUntilReady() {
    Awaitility.await()
        // Wait for 3 seconds for RabbitMQ to be fully initialized
        // This is prone to hardware specs, so adjust accordingly if needed
        .pollDelay(Duration.ofSeconds(getValue("RABBITMQ_POLL_DELAY", DEFAULT_POLL_DELAY)))
        .atMost(Duration.ofSeconds(getValue("RABBITMQ_TIMEOUT", DEFAULT_TIMEOUT)))
        .until(() -> waitStrategyTarget.execInContainer(COMMAND).getExitCode() == 0);
  }

  private static long getValue(String key, long defaultValue) {
    try {
      return Long.parseLong(System.getProperty(key));
    } catch (NumberFormatException _) {
      return defaultValue;
    }
  }
}
