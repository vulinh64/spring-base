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

  public PostCreationDTO(
      String title,
      String excerpt,
      String postContent,
      String slug,
      UUID categoryId,
      Set<String> tags) {
    this.title = title;
    this.excerpt = excerpt;
    this.postContent = postContent;
    this.slug = slug;
    this.categoryId = categoryId;
    this.tags = SetUtils.emptyIfNull(tags);
  }
}
