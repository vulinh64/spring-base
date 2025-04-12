package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.RoleData;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.*;

@With
@Builder
@JsonInclude(Include.NON_NULL)
public record SingleUserResponse(
    UUID id,
    String username,
    String fullName,
    String email,
    Boolean isActive,
    Instant createdDate,
    Instant updatedDate,
    Collection<RoleData> userRoles)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
