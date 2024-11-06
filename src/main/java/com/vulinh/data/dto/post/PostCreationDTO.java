package com.vulinh.data.dto.post;

import java.util.Set;
import java.util.UUID;
import lombok.With;
import org.apache.commons.collections4.SetUtils;

@With
public record PostCreationDTO(
    String title,
    String excerpt,
    String postContent,
    String slug,
    UUID categoryId,
    Set<String> tags) {

  public PostCreationDTO {
    tags = SetUtils.emptyIfNull(tags);
  }
}
