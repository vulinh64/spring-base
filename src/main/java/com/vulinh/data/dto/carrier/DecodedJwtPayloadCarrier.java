package com.vulinh.data.dto.carrier;

import module java.base;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record DecodedJwtPayloadCarrier(UUID userId, UUID sessionId) implements Serializable {}
