package com.vulinh.data.dto.security;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record TokenResponse(String accessToken) {}
