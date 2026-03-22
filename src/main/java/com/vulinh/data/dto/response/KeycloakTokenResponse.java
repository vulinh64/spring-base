package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(@JsonProperty("access_token") String accessToken) {}
