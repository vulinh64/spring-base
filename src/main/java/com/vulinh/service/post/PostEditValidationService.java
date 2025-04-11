package com.vulinh.service.post;

import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.Tag;
import com.vulinh.utils.post.PostUtils;
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

  public boolean isPostUnchanged(PostCreationRequest postCreationRequest, Post post) {
    var result =
        Objects.equals(
                PostUtils.normalizeText(postCreationRequest.title()),
                PostUtils.normalizeText(post.getTitle()))
            && Objects.equals(
                PostUtils.normalizeText(postCreationRequest.excerpt()),
                PostUtils.normalizeText(post.getExcerpt()))
            && (PostUtils.isUncategorizedPost(postCreationRequest, post)
                || Objects.equals(postCreationRequest.categoryId(), post.getCategory().getId()))
            && CollectionUtils.isEqualCollection(
                postCreationRequest.tags(),
                post.getTags().stream()
                    .map(Tag::getDisplayName)
                    .map(PostUtils::normalizeText)
                    .collect(Collectors.toSet()))
            && Objects.equals(
                StringUtils.normalizeSpace(postCreationRequest.postContent()),
                StringUtils.normalizeSpace(post.getPostContent()));

    if (!result) {
      log.debug("Post {} ({})  unchanged", post.getId(), post.getTitle());
    }

    return result;
  }
}
