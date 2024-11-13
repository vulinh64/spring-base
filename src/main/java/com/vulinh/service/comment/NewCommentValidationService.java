package com.vulinh.service.comment;

import com.vulinh.locale.CommonMessage;
import com.vulinh.data.dto.comment.NewCommentDTO;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class NewCommentValidationService {

  private static final int COMMENT_MAX_LENGTH = 10000;

  public void validate(NewCommentDTO newCommentDTO) {
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
    private final CommonMessage errorMessage;
    private final String additionalMessage;
    private final Object[] arguments = {};
  }
}
