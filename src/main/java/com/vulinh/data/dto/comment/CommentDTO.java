package com.vulinh.data.dto.comment;

import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDTO(
    UUID id,
    String content,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    String username,
    String fullName,
    Boolean isEdited)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
