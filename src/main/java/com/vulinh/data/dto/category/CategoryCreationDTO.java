package com.vulinh.data.dto.category;

import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@With
@Builder
public record CategoryCreationDTO(String displayName) {

  public CategoryCreationDTO {
    displayName = StringUtils.normalizeSpace(displayName);
  }
}
