package com.vulinh;

import com.vulinh.configuration.AuditorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = AuditorConfiguration.AUDITOR_PROVIDER)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class SpringBaseProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBaseProjectApplication.class, args);
  }
}
