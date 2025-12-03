package com.vulinh.data.dto.response;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record KeycloakUserResponse(
    String id,
    String username,
    String firstName,
    String lastName,
    String email,
    boolean isEnabled) {}
