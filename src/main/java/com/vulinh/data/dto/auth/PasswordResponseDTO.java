package com.vulinh.data.dto.auth;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record PasswordResponseDTO(String rawPassword, String encodedPassword) {}
