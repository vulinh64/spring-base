package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;

@Builder
public record PrefetchPostResponse(
    UUID id,
    String title,
    String excerpt,
    String slug,
    Instant createdDateTime,
    Instant updatedDateTime,
    UUID authorId,
    AuthorResponse author,
    CategoryResponse category)
    implements RecordUuidIdentifiable, Serializable {}
