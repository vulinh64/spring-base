package com.vulinh;

import com.vulinh.configuration.AuditorConfiguration;
import com.vulinh.configuration.SecurityConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class SpringBaseProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
