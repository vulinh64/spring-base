package com.vulinh.data.dto.post;

import java.util.Set;
import java.util.UUID;

import com.vulinh.utils.post.TitleCaseUtils;
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
    title = TitleCaseUtils.toTitleCase(title);
    tags = SetUtils.emptyIfNull(tags);
  }
}
