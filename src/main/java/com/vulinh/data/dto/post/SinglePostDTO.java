package com.vulinh.data.dto.post;

import com.vulinh.data.base.RecordUuidIdentifiable;
import com.vulinh.data.dto.category.CategoryDTO;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SinglePostDTO(
    UUID id,
    String title,
    String excerpt,
    String slug,
    String postContent,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    AuthorDTO author,
    CategoryDTO category,
    List<TagDTO> tags)
    implements RecordUuidIdentifiable, Serializable {}
