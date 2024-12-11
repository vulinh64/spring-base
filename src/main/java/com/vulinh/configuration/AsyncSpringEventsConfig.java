package com.vulinh.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Slf4j
@Configuration
public class AsyncSpringEventsConfig {

  @Bean
  public ApplicationEventMulticaster applicationEventMulticaster() {
    var eventMulticaster = new SimpleApplicationEventMulticaster();

    var taskExecutor = new SimpleAsyncTaskExecutor();

    // Use virtual threads only if Java version is 21
    // Not that it is necessary, but a safeguard nonetheless
    if (JavaVersion.getJavaVersion().isEqualOrNewerThan(JavaVersion.TWENTY_ONE)) {
      taskExecutor.setVirtualThreads(true);

      log.info("Virtual Threads possibility: YES");
    } else {
      log.info("Virtual Thread not supported, using thread pools...");
    }

    eventMulticaster.setTaskExecutor(taskExecutor);

    return eventMulticaster;
  }
}
