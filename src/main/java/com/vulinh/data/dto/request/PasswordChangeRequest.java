package com.vulinh.data.dto.request;

import java.io.Serializable;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PasswordChangeRequest(String oldPassword, String newPassword)
    implements Serializable {}
