package com.vulinh.data.dto.category;

import org.apache.commons.lang3.StringUtils;

public record CategoryCreationDTO(String displayName) {

  public CategoryCreationDTO {
    displayName =
        StringUtils.isNotBlank(displayName)
            ? StringUtils.capitalize(StringUtils.normalizeSpace(displayName))
            : displayName;
  }
}
