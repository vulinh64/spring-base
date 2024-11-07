package com.vulinh.utils.post;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.post.PostCreationDTO;
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

  public static PostCreationDTO getActualDTO(PostCreationDTO postCreationDTO) {
    var result =
        postCreationDTO.withTags(
            postCreationDTO.tags().stream()
                .filter(StringUtils::isNotBlank)
                // Convert all tags to lower case
                // Remove extra space
                .map(PostUtils::normalizeText)
                .collect(Collectors.toSet()));

    return StringUtils.isBlank(postCreationDTO.slug())
        ? result.withSlug(SlugUtils.createPostSlug(postCreationDTO.title()))
        : result;
  }

  public static boolean isUncategorizedPost(PostCreationDTO postCreationDTO, Post post) {
    return (postCreationDTO.categoryId() == null
            || CommonConstant.UNCATEGORIZED_ID.equals(postCreationDTO.categoryId()))
        && CommonConstant.UNCATEGORIZED_ID.equals(post.getCategory().getId());
  }
}
