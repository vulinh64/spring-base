package com.vulinh.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class AsyncSpringEventsConfig {

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
      var taskExecutor = new SimpleAsyncTaskExecutor();

      taskExecutor.setVirtualThreads(true);

      log.info("Virtual Threads possibility: YES");

      return taskExecutor;
    }

    log.info("Virtual Threads not supported, using ThreadPoolTaskExecutor...");

    return new ThreadPoolTaskExecutor();
  }
}
