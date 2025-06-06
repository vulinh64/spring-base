package com.vulinh.configuration;

import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.utils.SecurityUtils;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorConfiguration {

  public static final String AUDITOR_PROVIDER = "auditorProvider";

  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return () -> SecurityUtils.getUserDTO().map(UserBasicResponse::id);
  }
}
