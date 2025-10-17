package com.vulinh.service.comment;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFoundException;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import com.vulinh.utils.validator.ValidatorChain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewCommentValidationService {

  static final int COMMENT_MAX_LENGTH = 10000;

  private final CommentRepository commentRepository;

  public Comment validateEditComment(NewCommentRequest newCommentRequest, UUID commentId) {
    validate(newCommentRequest);

    var comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () ->
                    NotFoundException.entityNotFound(
                        CommonConstant.COMMENT_ENTITY,
                        commentId,
                        ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));

    var user = SecurityUtils.getUserDTOOrThrow();

    var createdBy = comment.getCreatedBy();

    if (!Objects.equals(createdBy.getId(), user.id())) {
      throw NoSuchPermissionException.noSuchPermissionException(
          "User [%s] cannot edit comment [%s] which belonged to user [%s]"
              .formatted(user.username(), comment.getId(), createdBy.getUsername()),
          ServiceErrorCode.MESSAGE_INVALID_OWNER_OR_NO_RIGHT);
    }

    return comment;
  }

  protected void validate(NewCommentRequest newCommentRequest) {
    ValidatorChain.<NewCommentRequest>start()
        .addValidator(NewCommentRule.values())
        .executeValidation(newCommentRequest);
  }

  @Getter
  @RequiredArgsConstructor
  public enum NewCommentRule implements NoArgsValidatorStep<NewCommentRequest> {
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

    private final Predicate<NewCommentRequest> predicate;
    private final ServiceErrorCode applicationError;
    private final String exceptionMessage;
  }
}
