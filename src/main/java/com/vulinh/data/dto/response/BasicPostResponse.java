package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.response.data.AuthorData;
import com.vulinh.data.dto.response.data.TagData;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@JsonInclude(Include.NON_NULL)
@With
@Builder
public record BasicPostResponse(
    UUID id,
    String title,
    String excerpt,
    String slug,
    Instant createdDate,
    Instant updatedDate,
    AuthorData author,
    CategoryResponse category,
    Collection<TagData> tags)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
