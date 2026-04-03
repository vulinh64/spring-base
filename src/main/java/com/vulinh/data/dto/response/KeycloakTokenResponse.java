package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") Integer expiresIn,
    @JsonProperty("refresh_expires_in") Integer refreshExpiresIn) {}
