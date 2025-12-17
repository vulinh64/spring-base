package com.vulinh.data.dto.projection;

import module java.base;

import com.vulinh.data.base.UUIDRevisionId;
import com.vulinh.data.entity.RevisionType;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PostRevisionProjection(
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
    Instant revisionCreatedDateTime,
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
