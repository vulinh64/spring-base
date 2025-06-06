package com.vulinh.service.post;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.NotFoundException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.post.PostUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostEditService {

  private final PostRepository postRepository;

  private final PostValidationService postValidationService;
  private final PostEditValidationService postEditValidationService;
  private final TagService tagService;
  private final CategoryService categoryService;

  @Transactional
  public Optional<Post> editPost(UUID postId, PostCreationRequest postCreationRequest) {
    postValidationService.validatePost(postCreationRequest);

    var userDTO = SecurityUtils.getUserDTOOrThrow();

    var actualPostCreationDTO = PostUtils.getActualDTO(postCreationRequest);

    var post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () ->
                    NotFoundException.entityNotFound(
                        CommonConstant.POST_ENTITY,
                        postId,
                        ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));

    postValidationService.validateModifyingPermission(userDTO, post);

    // Post unchanged
    if (postEditValidationService.isPostUnchanged(actualPostCreationDTO, post)) {
      return Optional.empty();
    }

    var category = categoryService.getCategory(postCreationRequest.categoryId());

    var tags = tagService.parseTags(actualPostCreationDTO);

    PostMapper.INSTANCE.merge(actualPostCreationDTO, category, tags, post);

    postRepository.save(post);

    return Optional.of(post);
  }
}
