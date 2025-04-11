package com.vulinh.data.dto.response;

import com.vulinh.data.base.UUIDRevisionId;
import com.vulinh.data.entity.RevisionType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PostRevisionResponse(
    UUID postId,
    Long revisionNumber,
    RevisionType revisionType,
    String title,
    String slug,
    String excerpt,
    String postContent,
    UUID authorId,
    UUID categoryId,
    String tags,
    LocalDateTime revisionCreatedDate,
    UUID revisionCreatedBy)
    implements UUIDRevisionId, Serializable {

  @Override
  public UUID getId() {
    return postId;
  }

  @Override
  public Long getRevisionNumber() {
    return revisionNumber;
  }
}
