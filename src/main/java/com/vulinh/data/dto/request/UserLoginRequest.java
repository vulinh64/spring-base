package com.vulinh.data.dto.request;

import lombok.Builder;
import lombok.With;

/// @deprecated Dedicate to KeyCloak
@With
@Builder
@Deprecated(forRemoval = true)
public record UserLoginRequest(String username, String password) {}
