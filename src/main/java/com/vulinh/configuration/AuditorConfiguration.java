package com.vulinh.configuration;

import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.UUID;

@Configuration
public class AuditorConfiguration {

  public static final String AUDITOR_PROVIDER = "auditorProvider";

  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return () -> SecurityUtils.getUserDTO(null).map(UserBasicDTO::id);
  }
}
