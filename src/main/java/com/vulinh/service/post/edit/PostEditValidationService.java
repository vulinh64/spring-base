package com.vulinh.service.post.edit;

import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.Tag;
import com.vulinh.utils.PostUtils;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEditValidationService {

  public boolean isPostUnchanged(PostCreationDTO postCreationDTO, Post post) {
    var result =
        Objects.equals(
                PostUtils.normalizeText(postCreationDTO.title()),
                PostUtils.normalizeText(post.getTitle()))
            && Objects.equals(
                PostUtils.normalizeText(postCreationDTO.excerpt()),
                PostUtils.normalizeText(post.getExcerpt()))
            && (PostUtils.isUncategorizedPost(postCreationDTO, post)
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

    if (!result) {
      log.debug("Post {} ({})  unchanged", post.getId(), post.getTitle());
    }

    return result;
  }
}
