package com.vulinh.data.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record JwtPayload(
    @JsonProperty("iss") String issuer, UUID userId, UUID sessionId, AccessTokenType tokenType)
    implements Serializable {}
