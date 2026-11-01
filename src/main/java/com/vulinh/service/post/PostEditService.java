package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.dto.request.PostEditRequest;
import com.vulinh.data.dto.request.PostRequest;
import com.vulinh.data.entity.Post;
import com.vulinh.data.entity.Tag;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.ApplicationValidationException;
import com.vulinh.exception.ResourceConflictException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.post.PostUtils;
import com.vulinh.utils.post.SlugUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEditService {

  final PostRepository postRepository;
  final EntityManager entityManager;

  final PostValidationService postValidationService;
  final TagService tagService;
  final CategoryService categoryService;

  @Transactional
  public Optional<Post> editPost(UUID postId, PostEditRequest postEditRequest) {
    if (postEditRequest.version() == null || postEditRequest.version() < 0) {
      throw new ApplicationValidationException(
          "A nonnegative post version is required", ServiceErrorCode.MESSAGE_POST_INVALID_VERSION);
    }

    postValidationService.validatePost(postEditRequest);

    var userDTO = SecurityUtils.getUserDTOOrThrow();

    var actualPostEditDTO = PostUtils.getActualDTO(postEditRequest);

    var post = postValidationService.getPost(postId);

    postValidationService.validateModifyingPermission(userDTO, post);

    // Check even for unchanged requests: a stale editor must review the latest snapshot.
    if (!Objects.equals(postEditRequest.version(), post.getVersion())) {
      throw ResourceConflictException.resourceConflictException(
          "Post %s has changed since it was loaded".formatted(postId),
          ServiceErrorCode.MESSAGE_POST_VERSION_CONFLICT);
    }

    // Resolve slug uniqueness only if slug changed
    if (!actualPostEditDTO.slug().equals(post.getSlug())) {
      var existingSlugs = postRepository.findSlugsByBaseSlug(actualPostEditDTO.slug());

      // Exclude the current post's slug from conflict detection
      existingSlugs.remove(post.getSlug());
      actualPostEditDTO =
          actualPostEditDTO.withSlug(
              SlugUtils.resolveUniqueSlug(actualPostEditDTO.slug(), existingSlugs));
    } else {
      actualPostEditDTO = actualPostEditDTO.withSlug(post.getSlug());
    }

    // Post unchanged
    if (isPostUnchanged(actualPostEditDTO, post)) {
      entityManager.lock(post, LockModeType.OPTIMISTIC);
      return Optional.empty();
    }

    var category = categoryService.getCategory(postEditRequest.categoryId());

    var tags = tagService.parseTags(actualPostEditDTO);

    PostMapper.INSTANCE.merge(actualPostEditDTO, category, tags, post);

    postRepository.save(post);

    return Optional.of(post);
  }

  private static boolean isPostUnchanged(PostRequest<?> postRequest, Post post) {
    var result =
        Objects.equals(
                PostUtils.normalizeText(postRequest.title()),
                PostUtils.normalizeText(post.getTitle()))
            && Objects.equals(
                PostUtils.normalizeText(postRequest.excerpt()),
                PostUtils.normalizeText(post.getExcerpt()))
            && (PostUtils.isUncategorizedPost(postRequest, post)
                || Objects.equals(postRequest.categoryId(), post.getCategory().getId()))
            && CollectionUtils.isEqualCollection(
                postRequest.tags(),
                post.getTags().stream()
                    .map(Tag::getDisplayName)
                    .map(PostUtils::normalizeText)
                    .collect(Collectors.toSet()))
            && Objects.equals(postRequest.postContent(), post.getPostContent())
            && Objects.equals(postRequest.slug(), post.getSlug());

    if (!result) {
      log.debug("Post {} ({})  unchanged", post.getId(), post.getTitle());
    }

    return result;
  }
}
