package com.vulinh.data.dto.security;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record AccessToken(
    String accessToken, OffsetDateTime issuedAt, OffsetDateTime expiration) {}
