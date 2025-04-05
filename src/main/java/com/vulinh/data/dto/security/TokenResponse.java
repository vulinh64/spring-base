package com.vulinh.data.dto.security;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record TokenResponse(String accessToken, String refreshToken) {}
