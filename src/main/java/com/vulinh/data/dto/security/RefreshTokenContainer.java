package com.vulinh.data.dto.security;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record RefreshTokenContainer(String refreshToken, Instant expirationDate)
    implements Serializable {}
