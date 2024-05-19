package com.vulinh.data.dto.post;

import com.vulinh.data.entity.RevisionType;
import java.io.Serializable;
import java.time.LocalDateTime;

public record PostRevisionDTO(
    String postId,
    Long revisionNumber,
    RevisionType revisionType,
    String title,
    String slug,
    String excerpt,
    String postContent,
    String authorId,
    String categoryId,
    String tags,
    LocalDateTime revisionCreatedDate,
    String revisionCreatedBy)
    implements Serializable {}
