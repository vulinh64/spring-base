package com.vulinh.data.dto.response;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.RoleData;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserBasicResponse(
    UUID id,
    UUID sessionId,
    String username,
    String fullName,
    String email,
    Instant createdDate,
    Instant updatedDate,
    Collection<RoleData> userRoles)
    implements RecordUuidIdentifiable, Serializable {}
