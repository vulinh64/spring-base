package com.vulinh.data.dto.request;

import com.vulinh.utils.TextSanitizer;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record NewCommentRequest(String content) {

  public NewCommentRequest {
    TextSanitizer.detectXss(content, "content");
  }
}
