package com.vulinh.data.dto.security;

import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record DecodedJwtPayload(UUID userId, UUID sessionId) implements Serializable {}
