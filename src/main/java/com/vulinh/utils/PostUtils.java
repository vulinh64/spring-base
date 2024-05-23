package com.vulinh.utils;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.post.create.PostCreationValidationService;
import java.security.SecureRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUtils {

  public static String normalizeText(@NonNull String text) {
    return StringUtils.normalizeSpace(text).toLowerCase();
  }

  public static String createPostSlug(@NonNull String title) {
    var randomNumber = generateRandomNumber();

    // Remove character accents, normalize white spaces and turn text to lowercase
    var result = "%s-%d".formatted(createBasicSlug(title), randomNumber);

    if (result.length() > PostCreationValidationService.SLUG_MAX_LENGTH) {
      throw ExceptionBuilder.buildCommonException(
          "Post's slug exceeded %d characters"
              .formatted(PostCreationValidationService.SLUG_MAX_LENGTH),
          CommonMessage.MESSAGE_POST_INVALID_SLUG);
    }

    return result;
  }

  @NonNull
  public static String createBasicSlug(@NonNull String text) {
    return StringUtils.stripAccents(PostUtils.normalizeText(text))
        // Replace white space to "-"
        .replace(' ', '-')
        // Replace Vietnamese character "đ" by "d"
        .replace("đ", "d");
  }

  private static int generateRandomNumber() {
    try {
      var result = SecureRandom.getInstanceStrong().nextInt();

      // Use positive value only
      return result == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(result);
    } catch (Exception exception) {
      throw ExceptionBuilder.buildCommonException(
          "Unable to generate random number", CommonMessage.MESSAGE_INTERNAL_ERROR, exception);
    }
  }

  public static PostCreationDTO getActualCreationDTO(PostCreationDTO postCreationDTO) {
    var slug = postCreationDTO.slug();

    return getActualPostDTO(
        postCreationDTO,
        () ->
            StringUtils.isBlank(slug)
                ? createPostSlug(postCreationDTO.title())
                : createPostSlug(slug));
  }

  public static PostCreationDTO getActualPostEditDTO(PostCreationDTO postCreationDTO) {
    var slug = postCreationDTO.slug();

    return getActualPostDTO(
        postCreationDTO,
        () -> StringUtils.isBlank(slug) ? createPostSlug(postCreationDTO.title()) : slug);
  }

  private static PostCreationDTO getActualPostDTO(
      PostCreationDTO postCreationDTO, Supplier<String> slugGenerator) {
    return postCreationDTO
        .withSlug(slugGenerator.get())
        .withTags(
            postCreationDTO.tags().stream()
                .filter(StringUtils::isNotBlank)
                // Convert all tags to lower case
                // Remove extra space
                .map(PostUtils::normalizeText)
                .collect(Collectors.toSet()));
  }

  public static boolean isUncategorizedPost(PostCreationDTO postCreationDTO, Post post) {
    return (StringUtils.isBlank(postCreationDTO.categoryId())
            || CommonConstant.UNCATEGORIZED_ID.equals(postCreationDTO.categoryId()))
        && CommonConstant.UNCATEGORIZED_ID.equals(post.getCategory().getId());
  }

  public static String capitalize(String text) {
    return StringUtils.capitalize(StringUtils.normalizeSpace(text));
  }
}
