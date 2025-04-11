package com.vulinh.data.dto.carrier;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record TokenResponse(String accessToken, String refreshToken) {}
