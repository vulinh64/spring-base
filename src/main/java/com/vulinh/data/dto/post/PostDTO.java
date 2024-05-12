package com.vulinh.data.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@JsonInclude(Include.NON_NULL)
public record PostDTO(
    String id,
    String title,
    String excerpt,
    String slug,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    AuthorDTO author,
    CategoryDTO category,
    Collection<TagDTO> tags)
    implements Serializable {}
