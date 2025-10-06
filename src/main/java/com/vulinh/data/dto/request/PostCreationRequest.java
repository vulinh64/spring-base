package com.vulinh.data.dto.request;

import module java.base;

import com.vulinh.utils.post.TitleCaseUtils;
import lombok.Builder;
import lombok.With;
import org.apache.commons.collections4.SetUtils;

@With
@Builder
public record PostCreationRequest(
    String title,
    String excerpt,
    String postContent,
    String slug,
    UUID categoryId,
    Set<String> tags) {

  public PostCreationRequest(String title, String excerpt, String postContent, String slug, Set<String> e) {
    this(title, excerpt, postContent, slug, null, e);
  }

  public PostCreationRequest {
    title = TitleCaseUtils.toTitleCase(title);
    tags = SetUtils.emptyIfNull(tags);
  }
}
