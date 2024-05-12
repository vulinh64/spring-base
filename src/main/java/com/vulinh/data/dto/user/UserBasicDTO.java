package com.vulinh.data.dto.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

public record UserBasicDTO(
    String id,
    String username,
    String fullName,
    String email,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    Collection<String> userRoles)
    implements Serializable {}
