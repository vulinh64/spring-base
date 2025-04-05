package com.vulinh.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class AsyncSpringEventsConfig {

  static final String THREAD_NAME_PREFIX = "event_executor_";

  @Bean
  public ApplicationEventMulticaster applicationEventMulticaster() {
    var eventMulticaster = new SimpleApplicationEventMulticaster();

    eventMulticaster.setTaskExecutor(getTaskExecutor());

    return eventMulticaster;
  }

  // Use virtual threads only if Java version is 21
  // Not that it is necessary, but a safeguard nonetheless
  private static TaskExecutor getTaskExecutor() {
    if (JavaVersion.getJavaVersion().isEqualOrNewerThan(JavaVersion.TWENTY_ONE)) {
      return new VirtualThreadTaskExecutor(THREAD_NAME_PREFIX);
    }

    log.info("Virtual Threads not supported, using ThreadPoolTaskExecutor...");

    var executor = new ThreadPoolTaskExecutor();

    executor.setThreadNamePrefix(THREAD_NAME_PREFIX);

    return executor;
  }
}
