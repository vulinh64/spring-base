package com.vulinh.data.dto.request;

import module java.base;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record PasswordChangeRequest(String oldPassword, String newPassword)
    implements Serializable {}
