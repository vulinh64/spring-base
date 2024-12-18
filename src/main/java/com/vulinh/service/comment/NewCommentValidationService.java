package com.vulinh.service.comment;

import com.vulinh.data.dto.comment.NewCommentDTO;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
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

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  private static final int COMMENT_MAX_LENGTH = 10000;

  public Comment validateEditComment(
      NewCommentDTO newCommentDTO, UUID postId, UUID commentId, HttpServletRequest request) {
    validateCreateComment(newCommentDTO, postId);

    var comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> ExceptionFactory.INSTANCE.commentNotFound(postId, commentId));

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

  public void validateCreateComment(NewCommentDTO newCommentDTO, UUID postId) {
    validate(newCommentDTO);

    if (!postRepository.existsById(postId)) {
      throw ExceptionFactory.INSTANCE.postNotFound(postId);
    }
  }

  protected void validate(NewCommentDTO newCommentDTO) {
    ValidatorChain.<NewCommentDTO>start()
        .addValidator(NewCommentRule.values())
        .executeValidation(newCommentDTO);
  }

  @Getter
  @RequiredArgsConstructor
  public enum NewCommentRule implements ValidatorStep<NewCommentDTO> {
    COMMENT_NOT_BLANK(
        ValidatorStepFactory.noBlankField(NewCommentDTO::content),
        CommonMessage.MESSAGE_COMMENT_INVALID,
        "Blank comment is not allowed"),
    COMMENT_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(NewCommentDTO::content, COMMENT_MAX_LENGTH),
        CommonMessage.MESSAGE_COMMENT_TOO_LONG,
        "Comment exceeded %s characters".formatted(COMMENT_MAX_LENGTH)) {

      @Override
      public Object[] getArguments() {
        return new Integer[] {COMMENT_MAX_LENGTH};
      }
    };

    private final Predicate<NewCommentDTO> predicate;
    private final CommonMessage error;
    private final String exceptionMessage;
    private final Object[] arguments = {};
  }
}
