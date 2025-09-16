package com.vulinh.data.dto.carrier;

import module java.base;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record RefreshTokenCarrier(String refreshToken, Instant expirationDate)
    implements Serializable {}
