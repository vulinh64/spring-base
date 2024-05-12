package com.vulinh.data.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Collection;
import lombok.Builder;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Builder
@With
public record JwtPayload(
    String id, @JsonProperty("sub") String subject, Collection<String> userRoles)
    implements Serializable {

  public JwtPayload(String id, String subject, Collection<String> userRoles) {
    this.id = id;
    this.subject = subject;
    this.userRoles = CollectionUtils.emptyIfNull(userRoles);
  }
}
