package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordInstantDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record SingleCommentResponse(
    UUID id, String content, Instant createdDateTime, Instant updatedDateTime, Boolean isEdited)
    implements RecordUuidIdentifiable, RecordInstantDateTimeAuditable {}
