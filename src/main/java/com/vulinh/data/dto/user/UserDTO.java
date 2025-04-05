package com.vulinh.data.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import lombok.*;

@With
@Builder
@JsonInclude(Include.NON_NULL)
public record UserDTO(
    UUID id,
    String username,
    String fullName,
    String email,
    Boolean isActive,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    Collection<RoleDTO> userRoles)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
