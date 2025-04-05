package com.vulinh.data.dto.security;

import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record DecodedJwtPayload(UUID userId, UUID sessionId) implements Serializable {}
