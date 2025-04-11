package com.vulinh.data.dto.request;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserSearchRequest(String identity, Collection<String> roles) {

  public UserSearchRequest {
    roles = Optional.ofNullable(roles).orElse(Collections.emptySet());
  }
}
