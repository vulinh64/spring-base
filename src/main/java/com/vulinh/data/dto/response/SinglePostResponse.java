package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.AuthorData;
import com.vulinh.data.dto.response.data.TagData;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record SinglePostResponse(
    UUID id,
    String title,
    String excerpt,
    String slug,
    String postContent,
    Instant createdDate,
    Instant updatedDate,
    AuthorData author,
    CategoryResponse category,
    List<TagData> tags)
    implements RecordUuidIdentifiable, Serializable {}
