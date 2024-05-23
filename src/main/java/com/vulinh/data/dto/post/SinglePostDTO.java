package com.vulinh.data.dto.post;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.vulinh.data.dto.category.CategoryDTO;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record SinglePostDTO(
    String id,
    String title,
    String excerpt,
    String slug,
    String postContent,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    AuthorDTO author,
    CategoryDTO category,
    List<TagDTO> tags)
    implements Serializable {}
