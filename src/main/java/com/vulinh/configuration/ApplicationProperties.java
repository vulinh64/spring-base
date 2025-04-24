package com.vulinh.configuration;

import java.time.Duration;
import java.util.List;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application-properties")
public record ApplicationProperties(
    SecurityProperties security, SchedulingTaskProperties schedule) {

  // Testability
  @Builder
  public record SecurityProperties(
      String publicKey,
      String privateKey,
      List<String> noAuthenticatedUrls,
      List<VerbUrl> allowedVerbUrls,
      Duration jwtDuration,
      Duration refreshJwtDuration,
      Duration passwordResetCodeDuration,
      String issuer) {}

  public record SchedulingTaskProperties(String cleanExpiredUserSessions) {}
}
