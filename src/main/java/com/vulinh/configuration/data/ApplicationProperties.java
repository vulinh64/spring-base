package com.vulinh.configuration.data;

import module java.base;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@ConfigurationProperties(prefix = "application-properties")
public record ApplicationProperties(
    SecurityProperties security,
    MessageTopic messageTopic,
    KeycloakAuthentication keycloakAuthentication) {

  // Testability
  @Builder
  public record SecurityProperties(
      List<String> noAuthenticatedUrls,
      List<VerbUrl> noAuthenticatedVerbUrls,
      List<VerbUrl> highPrivilegeVerbUrls,
      String issuerUri,
      String realmName,
      String clientName) {}

  public record VerbUrl(HttpMethod method, String url) {}

  public record MessageTopic(String newPostTopic) {}

  public record KeycloakAuthentication(
      String authServer, String username, String password, String clientId, String realm) {}
}
