package com.vulinh.data.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vulinh.data.base.RecordDateTimeAuditable;
import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.category.CategoryDTO;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@JsonInclude(Include.NON_NULL)
@With
@Builder
public record PostDTO(
    UUID id,
    String title,
    String excerpt,
    String slug,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    AuthorDTO author,
    CategoryDTO category,
    Collection<TagDTO> tags)
    implements RecordUuidIdentifiable, RecordDateTimeAuditable {}
