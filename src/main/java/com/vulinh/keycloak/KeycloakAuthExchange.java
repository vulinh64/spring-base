package com.vulinh.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

// This will no longer be needed, so who cares?
@HttpExchange
public interface KeycloakAuthExchange {

  record KeycloakTokenResponse(@JsonProperty("access_token") String accessToken) {}

  @PostExchange(
      url = "/protocol/openid-connect/token",
      contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  KeycloakTokenResponse getToken(
      @RequestPart("grant_type") String grantType,
      @RequestPart("client_id") String clientId,
      @RequestPart("username") String username,
      @RequestPart("password") String password);
}
