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
      "18.3-alpine3.23",
      args ->
          PromptMessageShellHealthCheckDelegate.builder()
              .containerDescription("PostgreSQL")
              .actualWaitStrategy(
                  new ShellStrategy().withCommand("pg_isready -U %s".formatted(args)))
              .build()),
  // Use alpine image to save space
  // The management-alpine is for local development debugging
  RABBIT_MQ(
      "rabbitmq",
      "4.2.4-alpine",
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
