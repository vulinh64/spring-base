package com.vulinh.data.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.entity.Users;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public record UserBasicDTO(
    UUID id,
    UUID sessionId,
    String username,
    String fullName,
    String email,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    Collection<RoleDTO> userRoles,
    @JsonIgnore Users user)
    implements RecordUuidIdentifiable, Serializable {}
