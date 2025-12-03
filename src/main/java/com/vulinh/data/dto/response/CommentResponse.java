package com.vulinh.data.dto.response;

import module java.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vulinh.data.base.UuidIdentifiable;
import com.vulinh.data.entity.CommentRevision;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record CommentResponse(UUID postId, UUID commentId, long revisionNumber)
    implements UuidIdentifiable {

  public static CommentResponse of(UUID postId, CommentRevision commentRevision) {
    var id = commentRevision.getId();

    return new CommentResponse(postId, id.getCommentId(), id.getRevisionNumber());
  }

  @JsonIgnore
  @Override
  public UUID getId() {
    return commentId;
  }
}
