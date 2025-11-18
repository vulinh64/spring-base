package com.vulinh.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class PasswordEncoderConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    log.info("Using BCryptPassword as password encoder");

    return new BCryptPasswordEncoder();
  }
}
