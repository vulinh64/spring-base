package com.vulinh.data.dto.category;

import com.vulinh.utils.PostUtils;
import org.apache.commons.lang3.StringUtils;

public record CategoryCreationDTO(String displayName) {

  public CategoryCreationDTO(String displayName) {
    this.displayName =
        StringUtils.isNotBlank(displayName) ? PostUtils.capitalize(displayName) : displayName;
  }
}
