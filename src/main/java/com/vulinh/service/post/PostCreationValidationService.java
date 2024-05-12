package com.vulinh.service.post;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostCreationValidationService {

  public static final int TITLE_MAX_LENGTH = 5000;
  public static final int SLUG_MAX_LENGTH = 5000;
  public static final int TAG_MAX_LENGTH = 1000;

  public void validatePost(@NonNull PostCreationDTO postCreationDTO) {
    ValidatorChain.<PostCreationDTO>of()
        .addValidator(PostRule.values())
        .executeValidation(postCreationDTO);
  }

  private static boolean isValidTags(PostCreationDTO dto) {
    for (var tag : dto.tags()) {
      if (StringUtils.isBlank(tag) || tag.length() > TAG_MAX_LENGTH) {
        log.debug("Tag {} is empty or too long", tag);

        return false;
      }
    }

    return true;
  }

  @Getter
  @RequiredArgsConstructor
  public enum PostRule implements ValidatorStep<PostCreationDTO> {
    POST_NO_BLANK_TITLE(
        ValidatorStep.noBlankField(PostCreationDTO::title),
        CommonMessage.MESSAGE_POST_INVALID_TITLE,
        "Blank title is not allowed"),
    POST_LONG_ENOUGH_TITLE(
        ValidatorStep.noExceededLength(PostCreationDTO::title, TITLE_MAX_LENGTH),
        CommonMessage.MESSAGE_POST_INVALID_TITLE,
        "Title length is too long"),
    POST_NO_EMPTY_CONTENT(
        ValidatorStep.noBlankField(PostCreationDTO::postContent),
        CommonMessage.MESSAGE_POST_INVALID_CONTENT,
        "Empty content is not allowed"),
    POST_NO_INVALID_TAG(
        PostCreationValidationService::isValidTags,
        CommonMessage.MESSAGE_POST_INVALID_TAG,
        "Empty tag or tag is too long");

    private final Predicate<PostCreationDTO> predicate;
    private final CommonMessage errorMessage;
    private final String additionalMessage;
  }
}
