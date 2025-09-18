package com.vulinh;

import com.vulinh.configuration.ApplicationProperties;
import com.vulinh.configuration.AuditorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableCaching
class SpringBaseProjectApplication {

  static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
