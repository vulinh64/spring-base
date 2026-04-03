package com.vulinh.service.keycloak;

import com.vulinh.configuration.data.ApplicationProperties.SecurityProperties;
import com.vulinh.data.dto.response.KeycloakTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface KeycloakAuthExchange {

  @PostExchange(
      url = "/protocol/openid-connect/token",
      contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  KeycloakTokenResponse getToken(
      @RequestPart("grant_type") String grantType,
      @RequestPart("client_id") String clientId,
      @RequestPart("username") String username,
      @RequestPart("password") String password);

  @PostExchange(
      url = "/protocol/openid-connect/token",
      contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  KeycloakTokenResponse refreshToken(
      @RequestPart("grant_type") String grantType,
      @RequestPart("client_id") String clientId,
      @RequestPart("refresh_token") String refreshToken);

  default KeycloakTokenResponse getToken(
      SecurityProperties security, String username, String password) {
    return getToken("password", security.clientName(), username, password);
  }

  default KeycloakTokenResponse refreshToken(SecurityProperties security, String refreshToken) {
    return refreshToken("refresh_token", security.clientName(), refreshToken);
  }
}
