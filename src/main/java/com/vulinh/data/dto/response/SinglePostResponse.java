package com.vulinh.data.dto.response;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.AuthorData;
import com.vulinh.data.dto.response.data.TagData;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    AuthorData author,
    CategoryResponse category,
    List<TagData> tags)
    implements RecordUuidIdentifiable, Serializable {}
