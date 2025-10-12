package com.vulinh.data.dto.request;

import module java.base;

import com.vulinh.utils.TextSanitizer;
import com.vulinh.utils.post.TitleCaseUtils;
import lombok.Builder;
import lombok.With;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record PostCreationRequest(
    String title,
    String excerpt,
    String postContent,
    String slug,
    UUID categoryId,
    Set<String> tags) {

  // Testing only
  public PostCreationRequest(String title, String excerpt, String postContent, String slug, Set<String> e) {
    this(title, excerpt, postContent, slug, null, e);
  }

  public PostCreationRequest {
    title = TextSanitizer.sanitize(TitleCaseUtils.toTitleCase(title));
    excerpt = TextSanitizer.sanitize(StringUtils.normalizeSpace(excerpt));
    postContent = TextSanitizer.sanitize(StringUtils.normalizeSpace(postContent));
    slug = TextSanitizer.sanitize(StringUtils.normalizeSpace(slug));
    tags = SetUtils.emptyIfNull(tags).stream().map(TextSanitizer::sanitize).collect(Collectors.toSet());
  }
}
