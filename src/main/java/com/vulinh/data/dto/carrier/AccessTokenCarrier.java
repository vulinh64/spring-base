package com.vulinh.data.dto.carrier;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record AccessTokenCarrier(
    TokenResponse tokenResponse, UUID userId, UUID sessionId, Instant refreshTokenExpirationDate)
    implements Serializable {}
