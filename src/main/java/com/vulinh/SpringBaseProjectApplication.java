package com.vulinh;

import com.vulinh.configuration.AuditorConfiguration;
import com.vulinh.configuration.data.SchedulingTaskProperties;
import com.vulinh.configuration.data.SecurityConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({SecurityConfigProperties.class, SchedulingTaskProperties.class})
@EnableCaching
public class SpringBaseProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
