package com.vulinh.configuration;

import module java.base;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@ConfigurationProperties(prefix = "application-properties")
public record ApplicationProperties(
    SecurityProperties security, SchedulingTaskProperties schedule) {

  // Testability
  @Builder
  public record SecurityProperties(
      String publicKey,
      String privateKey,
      List<String> noAuthenticatedUrls,
      List<VerbUrl> noAuthenticatedVerbUrls,
      List<String> highPrivilegeUrls,
      List<VerbUrl> highPrivilegeVerbUrls,
      Duration jwtDuration,
      Duration refreshJwtDuration,
      Duration passwordResetCodeDuration,
      String issuer) {}

  public record SchedulingTaskProperties(String cleanExpiredUserSessions) {}

  public record VerbUrl(HttpMethod method, String url) {}
}
