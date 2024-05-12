package com.vulinh.data.dto.auth;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record PasswordRequestDTO(String rawPassword) {}
