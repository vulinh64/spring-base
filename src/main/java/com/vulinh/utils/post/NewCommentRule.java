package com.vulinh.utils.post;

import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NewCommentRule implements NoArgsValidatorStep<NewCommentReplyDTO> {
  COMMENT_NOT_BLANK(
      ValidatorStepFactory.noBlankField(NewCommentReplyDTO::content),
      CommonMessage.MESSAGE_COMMENT_INVALID,
      "Blank comment is not allowed"),
  COMMENT_NOT_TOO_LONG(
      ValidatorStepFactory.noExceededLength(NewCommentReplyDTO::content, PostUtils.COMMENT_MAX_LENGTH),
      CommonMessage.MESSAGE_COMMENT_TOO_LONG,
      "Comment exceeded %s characters".formatted(PostUtils.COMMENT_MAX_LENGTH)) {

    @Override
    public Object[] getArguments() {
      return new Integer[] {PostUtils.COMMENT_MAX_LENGTH};
    }
  };

  private final Predicate<NewCommentReplyDTO> predicate;
  private final CommonMessage error;
  private final String exceptionMessage;
}
