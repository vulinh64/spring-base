package com.vulinh.data.dto.carrier;

import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record RefreshTokenCarrier(String refreshToken, Instant expirationDate)
    implements Serializable {}
