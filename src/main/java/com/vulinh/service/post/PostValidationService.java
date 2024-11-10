package com.vulinh.service.post;

import com.vulinh.constant.CommonConstant;
import com.vulinh.constant.UserRole;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.user.RoleDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.exception.CommonException;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostValidationService {

  private static final int TITLE_MAX_LENGTH = 5000;
  private static final int TAG_MAX_LENGTH = 1000;

  public static CommonException postOrSlugNotFound(UUID postId) {
    return ExceptionFactory.INSTANCE.entityNotFound(
        "Post with ID %s or slug %s not found".formatted(postId, postId),
        CommonConstant.POST_ENTITY);
  }

  public static final ValidatorChain<PostCreationDTO> BASIC_POST_VALIDATOR =
      ValidatorChain.<PostCreationDTO>start().addValidator(PostRule.values());

  public void validateModifyingPermission(UserBasicDTO userDTO, Post post) {
    if (!(PostValidationService.isOwner(userDTO, post)
        || PostValidationService.isPowerUser(userDTO))) {
      throw ExceptionFactory.INSTANCE.buildCommonException(
          "Invalid author or no permission to edit",
          CommonMessage.MESSAGE_INVALID_OWNER_OR_NO_RIGHT);
    }
  }

  public static boolean isOwner(UserBasicDTO userDTO, Post post) {
    var userId = userDTO.id();
    var postAuthor = post.getAuthor();
    var postAuthorId = postAuthor.getId();

    var result = Objects.equals(userId, postAuthorId);

    if (!result) {
      log.debug(
          "User {} ({}) is not the owner of post ({}) {} (actual owner: {} ({}))",
          userId,
          userDTO.username(),
          post.getId(),
          post.getTitle(),
          postAuthorId,
          postAuthor.getUsername());
    }

    return result;
  }

  public static boolean isPowerUser(UserBasicDTO userDTO) {
    var maximumRole =
        userDTO.userRoles().stream()
            .mapToInt(RoleDTO::superiority)
            .max()
            .orElse(UserRole.USER.superiority());

    var result = maximumRole >= UserRole.POWER_USER.superiority();

    if (!result) {
      log.debug(
          "User {} ({}) with role {} is not a power user!",
          userDTO.id(),
          userDTO.username(),
          userDTO.userRoles().stream().map(RoleDTO::id).collect(Collectors.toSet()));
    }

    return result;
  }

  public void validatePost(@NonNull PostCreationDTO postCreationDTO) {
    BASIC_POST_VALIDATOR.executeValidation(postCreationDTO);
  }

  @Getter
  @RequiredArgsConstructor
  public enum PostRule implements ValidatorStep<PostCreationDTO> {
    POST_NO_BLANK_TITLE(
        ValidatorStepFactory.noBlankField(PostCreationDTO::title),
        CommonMessage.MESSAGE_POST_INVALID_TITLE,
        "Blank title is not allowed"),
    POST_LONG_ENOUGH_TITLE(
        ValidatorStepFactory.noExceededLength(PostCreationDTO::title, TITLE_MAX_LENGTH),
        CommonMessage.MESSAGE_POST_INVALID_TITLE,
        "Title length is too long"),
    POST_NO_EMPTY_CONTENT(
        ValidatorStepFactory.noBlankField(PostCreationDTO::postContent),
        CommonMessage.MESSAGE_POST_INVALID_CONTENT,
        "Empty content is not allowed"),
    POST_NO_INVALID_TAG(
        PostRule::isValidTags,
        CommonMessage.MESSAGE_POST_INVALID_TAG,
        "Empty tag or tag is too long");

    private final Predicate<PostCreationDTO> predicate;
    private final CommonMessage errorMessage;
    private final String additionalMessage;
    private final Object[] arguments = {};

    private static boolean isValidTags(PostCreationDTO dto) {
      for (var tag : dto.tags()) {
        if (StringUtils.isBlank(tag) || tag.length() > TAG_MAX_LENGTH) {
          log.debug("Tag {} is empty or too long", tag);

          return false;
        }
      }

      return true;
    }
  }
}
