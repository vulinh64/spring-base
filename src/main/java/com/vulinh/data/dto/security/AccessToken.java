package com.vulinh.data.dto.security;

import java.time.temporal.Temporal;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record AccessToken(String accessToken, Temporal issuedAt, Temporal expiration) {}
