package com.vulinh.data.dto.user;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserBasicDTO(
    UUID id,
    UUID sessionId,
    String username,
    String fullName,
    String email,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    Collection<RoleDTO> userRoles)
    implements RecordUuidIdentifiable, Serializable {}
