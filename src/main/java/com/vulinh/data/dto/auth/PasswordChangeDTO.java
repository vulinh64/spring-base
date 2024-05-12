package com.vulinh.data.dto.auth;

import java.io.Serializable;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PasswordChangeDTO(String oldPassword, String newPassword)
    implements Serializable {}
