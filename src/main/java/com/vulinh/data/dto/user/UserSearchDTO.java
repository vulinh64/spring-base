package com.vulinh.data.dto.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record UserSearchDTO(String identity, Collection<String> roles) {

  public UserSearchDTO {
    roles = Optional.ofNullable(roles).orElse(Collections.emptySet());
  }
}
