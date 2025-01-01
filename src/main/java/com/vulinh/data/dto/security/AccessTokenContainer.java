package com.vulinh.data.dto.security;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record AccessTokenContainer(
    TokenResponse tokenResponse, UUID userId, UUID sessionId, Instant refreshTokenExpirationDate)
    implements Serializable {}
