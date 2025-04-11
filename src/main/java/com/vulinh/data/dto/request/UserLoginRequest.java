package com.vulinh.data.dto.request;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserLoginRequest(String username, String password) {}
