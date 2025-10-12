package com.vulinh.data.dto.request;

import com.vulinh.utils.TextSanitizer;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record CategoryCreationRequest(String displayName) {

  public CategoryCreationRequest {
    displayName = TextSanitizer.sanitize(StringUtils.normalizeSpace(displayName));
  }
}
