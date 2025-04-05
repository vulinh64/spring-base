package com.vulinh.data.dto.auth;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserLoginDTO(String username, String password) {}
