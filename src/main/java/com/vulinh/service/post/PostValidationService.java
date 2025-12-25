package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Post;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFound404Exception;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorChainBuilder;
import com.vulinh.utils.validator.ValidatorStep;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostValidationService {

  final PostRepository postRepository;

  static final int TITLE_MAX_LENGTH = 5000;
  static final int TAG_MAX_LENGTH = 1000;

  public static final ValidatorChain<PostCreationRequest> BASIC_POST_VALIDATOR =
      new ValidatorChainBuilder<PostCreationRequest>().add(PostRule.values()).build();

  public void validateModifyingPermission(UserBasicResponse userDTO, Post post) {
    if (!(PostValidationService.isOwner(userDTO, post)
        || PostValidationService.isPowerUser(userDTO))) {
      throw NoSuchPermissionException.noSuchPermissionException(
          "Invalid authorId or no permission to edit",
          ServiceErrorCode.MESSAGE_INVALID_OWNER_OR_NO_RIGHT);
    }
  }

  public static boolean isOwner(UserBasicResponse userDTO, Post post) {
    var userId = userDTO.id();
    var postAuthorId = post.getAuthorId();

    var result = Objects.equals(userId, postAuthorId);

    if (!result) {
      log.debug(
          "User {} ({}) is not the owner of post ({}) {} (actual owner: {})",
          userId,
          userDTO.username(),
          post.getId(),
          post.getTitle(),
          postAuthorId);
    }

    return result;
  }

  public static boolean isPowerUser(UserBasicResponse userDTO) {
    var maximumRole =
        userDTO.userRoles().stream()
            .mapToInt(UserRole::superiority)
            .max()
            .orElse(UserRole.USER.superiority());

    var result = maximumRole >= UserRole.POWER_USER.superiority();

    if (!result) {
      log.debug(
          "User {} ({}) with role {} is not a power user!",
          userDTO.id(),
          userDTO.username(),
          userDTO.userRoles().stream().map(UserRole::name).collect(Collectors.toSet()));
    }

    return result;
  }

  public void validatePost(@NonNull PostCreationRequest postCreationRequest) {
    BASIC_POST_VALIDATOR.validate(postCreationRequest);
  }

  public Post getPost(UUID postId) {
    return postRepository
        .findById(postId)
        .orElseThrow(() -> NotFound404Exception.postNotFound(postId));
  }

  @Getter
  @RequiredArgsConstructor
  public enum PostRule implements ValidatorStep<PostCreationRequest> {
    POST_NO_BLANK_TITLE(
        ValidatorStepFactory.noBlankField(PostCreationRequest::title),
        ServiceErrorCode.MESSAGE_POST_INVALID_TITLE,
        "Blank title is not allowed"),
    POST_LONG_ENOUGH_TITLE(
        ValidatorStepFactory.noExceededLength(PostCreationRequest::title, TITLE_MAX_LENGTH),
        ServiceErrorCode.MESSAGE_POST_INVALID_TITLE,
        "Title length is too long"),
    POST_NO_EMPTY_CONTENT(
        ValidatorStepFactory.noBlankField(PostCreationRequest::postContent),
        ServiceErrorCode.MESSAGE_POST_INVALID_CONTENT,
        "Empty content is not allowed"),
    POST_NO_INVALID_TAG(
        PostRule::isValidTags,
        ServiceErrorCode.MESSAGE_POST_INVALID_TAG,
        "Empty tag or tag is too long");

    final Predicate<PostCreationRequest> predicate;
    final ServiceErrorCode applicationError;
    final String exceptionMessage;

    static boolean isValidTags(PostCreationRequest dto) {
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
