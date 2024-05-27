package com.vulinh;

import com.vulinh.configuration.AuditorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
public class SpringBaseProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
