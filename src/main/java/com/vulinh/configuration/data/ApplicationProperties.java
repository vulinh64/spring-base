package com.vulinh.configuration.data;

import module java.base;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@ConfigurationProperties(prefix = "application-properties")
public record ApplicationProperties(SecurityProperties security, MessageTopic messageTopic) {

  // Testability
  @Builder
  public record SecurityProperties(
      List<String> noAuthenticatedUrls,
      List<VerbUrl> noAuthenticatedVerbUrls,
      List<String> highPrivilegeUrls,
      List<VerbUrl> highPrivilegeVerbUrls,
      String issuerUri,
      String realmName,
      String clientName) {}

  public record VerbUrl(HttpMethod method, String url) {}

  public record MessageTopic(String newPostTopic) {}
}
