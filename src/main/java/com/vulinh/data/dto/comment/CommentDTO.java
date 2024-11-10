package com.vulinh.data.dto.comment;

import com.vulinh.data.RecordDateTimeAuditable;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDTO(
    UUID id,
    String content,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    String username,
    String fullName)
    implements RecordDateTimeAuditable {}
