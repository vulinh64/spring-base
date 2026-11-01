package com.vulinh.utils.post;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.PostRequest;
import com.vulinh.data.entity.Post;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostUtils {

  public static String normalizeText(@NonNull String text) {
    return StringUtils.normalizeSpace(text).toLowerCase();
  }

  public static <T extends PostRequest<T>> T getActualDTO(T postRequest) {
    return postRequest.withTags(normalizeTags(postRequest)).withSlug(normalizeSlug(postRequest));
  }

  private static Set<String> normalizeTags(PostRequest<?> postRequest) {
    return postRequest.tags().stream()
        .filter(StringUtils::isNotBlank)
        // Convert all tags to lower case
        // Remove extra space
        .map(PostUtils::normalizeText)
        .collect(Collectors.toSet());
  }

  private static String normalizeSlug(PostRequest<?> postRequest) {
    var slug = postRequest.slug();

    return StringUtils.isBlank(slug)
        ? SlugUtils.createPostSlug(postRequest.title())
        : SlugUtils.createBasicSlug(slug);
  }

  public static boolean isUncategorizedPost(PostRequest<?> postCreationRequest, Post post) {
    return (postCreationRequest.categoryId() == null
            || CommonConstant.NIL_UUID.equals(postCreationRequest.categoryId()))
        && CommonConstant.NIL_UUID.equals(post.getCategory().getId());
  }
}
