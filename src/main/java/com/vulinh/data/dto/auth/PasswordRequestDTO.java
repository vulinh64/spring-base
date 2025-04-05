package com.vulinh.data.dto.auth;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record PasswordRequestDTO(String rawPassword) {}
