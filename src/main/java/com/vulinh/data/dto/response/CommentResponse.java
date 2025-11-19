package com.vulinh.data.dto.response;

import com.vulinh.data.entity.CommentRevision;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record CommentResponse(UUID postId, UUID commentId, long revisionNumber) {

  public static CommentResponse of(UUID postId, CommentRevision commentRevision) {
    var id = commentRevision.getId();

    return new CommentResponse(postId, id.getCommentId(), id.getRevisionNumber());
  }
}
