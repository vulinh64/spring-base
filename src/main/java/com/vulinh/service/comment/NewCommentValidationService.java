package com.vulinh.service.comment;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.post.NewCommentRule;
import com.vulinh.utils.validator.ValidatorChain;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewCommentValidationService {

  private final CommentRepository commentRepository;

  public Comment validateReply(NewCommentReplyDTO newCommentReplyDTO, UUID commentId) {
    validate(newCommentReplyDTO);

    return getComment(commentId);
  }

  public Comment validateEditComment(
      NewCommentReplyDTO newCommentReplyDTO, UUID commentId, HttpServletRequest request) {
    validate(newCommentReplyDTO);

    var comment = getComment(commentId);

    var user = SecurityUtils.getUserDTOOrThrow(request);

    var createdBy = comment.getCreatedBy();

    if (!Objects.equals(createdBy.getId(), user.id())) {
      throw ExceptionFactory.INSTANCE.buildCommonException(
          "User [%s] cannot edit comment %s belonged to user [%s]"
              .formatted(user.username(), comment.getId(), createdBy.getUsername()),
          CommonMessage.MESSAGE_INVALID_OWNER_OR_NO_RIGHT);
    }

    return comment;
  }

  private Comment getComment(UUID commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(
            () ->
                ExceptionFactory.INSTANCE.entityNotFound(
                    "Comment ID %s not found".formatted(commentId), CommonConstant.COMMENT_ENTITY));
  }

  protected void validate(NewCommentReplyDTO newCommentReplyDTO) {
    ValidatorChain.<NewCommentReplyDTO>start()
        .addValidator(NewCommentRule.values())
        .executeValidation(newCommentReplyDTO);
  }
}
