package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.constant.UserRole;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserBasicResponse(
    UUID id,
    String username,
    String email,
    Collection<UserRole> userRoles)
    implements RecordUuidIdentifiable, Serializable {}
