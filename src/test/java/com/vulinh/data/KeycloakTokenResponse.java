package com.vulinh.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken) {}
