package com.vulinh.utils.post;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.entity.Post;
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

  public static PostCreationRequest getActualDTO(PostCreationRequest postCreationRequest) {
    var result =
        postCreationRequest.withTags(
            postCreationRequest.tags().stream()
                .filter(StringUtils::isNotBlank)
                // Convert all tags to lower case
                // Remove extra space
                .map(PostUtils::normalizeText)
                .collect(Collectors.toSet()));

    return StringUtils.isBlank(postCreationRequest.slug())
        ? result.withSlug(SlugUtils.createPostSlug(postCreationRequest.title()))
        : result;
  }

  public static boolean isUncategorizedPost(PostCreationRequest postCreationRequest, Post post) {
    return (postCreationRequest.categoryId() == null
            || CommonConstant.NIL_UUID.equals(postCreationRequest.categoryId()))
        && CommonConstant.NIL_UUID.equals(post.getCategory().getId());
  }
}
