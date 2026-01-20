package com.vulinh;

import com.vulinh.aspect.ExecutionTimeAspect;
import com.vulinh.configuration.AuditorConfiguration;
import com.vulinh.configuration.data.ApplicationProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableAsync
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Import(ExecutionTimeAspect.class)
class SpringBaseProjectApplication {

  static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
