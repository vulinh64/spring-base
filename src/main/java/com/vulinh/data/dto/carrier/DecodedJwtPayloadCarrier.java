package com.vulinh.data.dto.carrier;

import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record DecodedJwtPayloadCarrier(UUID userId, UUID sessionId) implements Serializable {}
