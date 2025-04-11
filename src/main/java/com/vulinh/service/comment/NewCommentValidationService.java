package com.vulinh.service.comment;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import com.vulinh.utils.validator.ValidatorChain;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewCommentValidationService {

  private final CommentRepository commentRepository;

  private static final int COMMENT_MAX_LENGTH = 10000;

  public Comment validateEditComment(
      NewCommentRequest newCommentRequest, UUID commentId, HttpServletRequest request) {
    validate(newCommentRequest);

    var comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () ->
                    ExceptionFactory.INSTANCE.entityNotFound(
                        "Comment ID %s not found".formatted(commentId),
                        CommonConstant.COMMENT_ENTITY));

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
        CommonMessage.MESSAGE_COMMENT_INVALID,
        "Blank comment is not allowed"),
    COMMENT_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(NewCommentRequest::content, COMMENT_MAX_LENGTH),
        CommonMessage.MESSAGE_COMMENT_TOO_LONG,
        "Comment exceeded %s characters".formatted(COMMENT_MAX_LENGTH)) {

      @Override
      public Object[] getArguments() {
        return new Integer[] {COMMENT_MAX_LENGTH};
      }
    };

    private final Predicate<NewCommentRequest> predicate;
    private final CommonMessage error;
    private final String exceptionMessage;
  }
}
