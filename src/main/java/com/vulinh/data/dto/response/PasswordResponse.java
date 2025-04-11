package com.vulinh.data.dto.response;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record PasswordResponse(String rawPassword, String encodedPassword) {}
