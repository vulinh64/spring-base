package com.vulinh.service.post.edit;

import com.vulinh.constant.CommonConstant;
import com.vulinh.constant.UserRole;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.user.RoleDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.Tag;
import com.vulinh.utils.PostUtils;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStepImpl;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEditValidationService {

  public void validateEditPermission(
      PostCreationDTO postCreationDTO, UserBasicDTO userDTO, Post post) {
    ValidatorChain.<PostCreationDTO>of()
        .addValidator(
            ValidatorStepImpl.of(
                isPowerUser(userDTO).or(isOwner(userDTO, post)),
                CommonMessage.MESSAGE_INVALID_OWNER_OR_NO_RIGHT,
                "Invalid author or not a power user"))
        .executeValidation(postCreationDTO);
  }

  public boolean isPostUnchanged(PostCreationDTO postCreationDTO, Post post) {
    return Objects.equals(
            PostUtils.normalizeText(postCreationDTO.title()),
            PostUtils.normalizeText(post.getTitle()))
        && Objects.equals(
            PostUtils.normalizeText(postCreationDTO.excerpt()),
            PostUtils.normalizeText(post.getExcerpt()))
        && (isUncategorizedPost(postCreationDTO, post)
            || Objects.equals(postCreationDTO.categoryId(), post.getCategory().getId()))
        && CollectionUtils.isEqualCollection(
            postCreationDTO.tags(),
            post.getTags().stream()
                .map(Tag::getDisplayName)
                .map(PostUtils::normalizeText)
                .collect(Collectors.toSet()))
        && Objects.equals(
            StringUtils.normalizeSpace(postCreationDTO.postContent()),
            StringUtils.normalizeSpace(post.getPostContent()));
  }

  public Predicate<PostCreationDTO> isOwner(UserBasicDTO userDTO, Post post) {
    return ignored -> {
      var userId = userDTO.id();
      var postId = post.getAuthor().getId();

      var result = Objects.equals(userId, postId);

      if (!result) {
        log.debug(
            "User {} ({}) is not the owner of post ({}) {}",
            userId,
            userDTO.username(),
            post.getId(),
            post.getTitle());
      }

      return result;
    };
  }

  public Predicate<PostCreationDTO> isPowerUser(UserBasicDTO userDTO) {
    return ignored -> {
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
    };
  }

  private static boolean isUncategorizedPost(PostCreationDTO postCreationDTO, Post post) {
    return (StringUtils.isBlank(postCreationDTO.categoryId())
            || CommonConstant.UNCATEGORIZED_ID.equals(postCreationDTO.categoryId()))
        && CommonConstant.UNCATEGORIZED_ID.equals(post.getCategory().getId());
  }
}
