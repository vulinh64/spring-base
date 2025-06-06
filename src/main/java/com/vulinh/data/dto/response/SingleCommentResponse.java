package com.vulinh.data.dto.response;

import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record SingleCommentResponse(
    UUID id,
    String content,
    Instant createdDate,
    Instant updatedDate,
    String username,
    String fullName,
    Boolean isEdited)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
