package com.vulinh.data.dto.response;

import module java.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.RecordInstantDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.TagData;
import lombok.Builder;
import lombok.With;

@JsonInclude(Include.NON_NULL)
@With
@Builder
public record BasicPostResponse(
    UUID id,
    Long revisionNumber,
    String title,
    String excerpt,
    String slug,
    Instant createdDateTime,
    Instant updatedDateTime,
    UUID authorId,
    CategoryResponse category,
    Collection<TagData> tags)
    implements RecordUuidIdentifiable, RecordInstantDateTimeAuditable {}
