package com.vulinh.service.comment;

import module java.base;

import com.vulinh.data.constant.EntityType;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFound404Exception;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorChainBuilder;
import com.vulinh.utils.validator.ValidatorStep;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewCommentValidationService {

  static final int COMMENT_MAX_LENGTH = 10000;
  
  static final ValidatorChain<NewCommentRequest> NEW_COMMENT_VALIDATOR =
      new ValidatorChainBuilder<NewCommentRequest>().add(NewCommentRule.values()).build();

  final CommentRepository commentRepository;

  public Comment validateEditComment(NewCommentRequest newCommentRequest, UUID commentId) {
    validate(newCommentRequest);

    var comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () ->
                    NotFound404Exception.entityNotFound(
                        EntityType.COMMENT, commentId, ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));

    var user = SecurityUtils.getUserDTOOrThrow();

    var createdBy = comment.getCreatedBy();

    if (!Objects.equals(createdBy, user.id())) {
      throw NoSuchPermissionException.noSuchPermissionException(
          "User [%s] cannot edit comment [%s] which belonged to user [%s]"
              .formatted(user.username(), comment.getId(), createdBy),
          ServiceErrorCode.MESSAGE_INVALID_OWNER_OR_NO_RIGHT);
    }

    return comment;
  }

  protected void validate(NewCommentRequest newCommentRequest) {
    NEW_COMMENT_VALIDATOR.validate(newCommentRequest);
  }

  @Getter
  @RequiredArgsConstructor
  public enum NewCommentRule implements ValidatorStep<NewCommentRequest> {
    COMMENT_NOT_BLANK(
        ValidatorStepFactory.noBlankField(NewCommentRequest::content),
        ServiceErrorCode.MESSAGE_COMMENT_INVALID,
        "Blank comment is not allowed"),
    COMMENT_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(NewCommentRequest::content, COMMENT_MAX_LENGTH),
        ServiceErrorCode.MESSAGE_COMMENT_TOO_LONG,
        "Comment exceeded %s characters".formatted(COMMENT_MAX_LENGTH)) {

      @Override
      public Object[] getArgs() {
        return new Integer[] {COMMENT_MAX_LENGTH};
      }
    };

    final Predicate<NewCommentRequest> predicate;
    final ServiceErrorCode applicationError;
    final String exceptionMessage;
  }
}
