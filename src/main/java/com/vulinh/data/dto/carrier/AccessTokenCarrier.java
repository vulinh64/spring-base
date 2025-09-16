package com.vulinh.data.dto.carrier;

import module java.base;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record AccessTokenCarrier(
    TokenResponse tokenResponse, UUID userId, UUID sessionId, Instant refreshTokenExpirationDate)
    implements Serializable {}
