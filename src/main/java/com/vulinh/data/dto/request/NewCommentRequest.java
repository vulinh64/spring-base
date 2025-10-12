package com.vulinh.data.dto.request;

import com.vulinh.utils.TextSanitizer;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record NewCommentRequest(String content) {

  public NewCommentRequest {
    content = TextSanitizer.sanitize(StringUtils.normalizeSpace(content));
  }
}
