package com.vulinh.utils.healthcheck;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.testcontainers.containers.wait.strategy.ShellStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

@RequiredArgsConstructor
@Builder
public class PromptMessageShellHealthCheckDelegate extends ShellStrategy {

  private final String containerDescription;
  private final WaitStrategy actualWaitStrategy;

  @Override
  protected void waitUntilReady() {
    // SONARSUPPRESS: Allow printing to console for health check purposes
    IO.println(
        "HEALTH CHECK: Container %s health check is in progress..."
            .formatted(containerDescription));

    actualWaitStrategy.waitUntilReady(waitStrategyTarget);

    // SONARSUPPRESS: Allow printing to console for health check purposes
    IO.println("HEALTH CHECK: Container %s is ready".formatted(containerDescription));
  }
}
