package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.RoleData;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserBasicResponse(
    UUID id,
    String username,
    String email,
    Collection<RoleData> userRoles)
    implements RecordUuidIdentifiable, Serializable {}
