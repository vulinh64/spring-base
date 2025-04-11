package com.vulinh.data.dto.request;

import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record CategoryCreationRequest(String displayName) {

  public CategoryCreationRequest {
    displayName = StringUtils.normalizeSpace(displayName);
  }
}
