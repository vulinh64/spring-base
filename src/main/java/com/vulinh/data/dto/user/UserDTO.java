package com.vulinh.data.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.*;

@Builder
@With
@JsonInclude(Include.NON_NULL)
public record UserDTO(
    String id,
    String username,
    String fullName,
    String email,
    Boolean isActive,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    Collection<RoleDTO> userRoles)
    implements Serializable {}
