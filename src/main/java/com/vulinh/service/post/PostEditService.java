package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.post.PostUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostEditService {

  final PostRepository postRepository;

  final PostValidationService postValidationService;
  final PostEditValidationService postEditValidationService;
  final TagService tagService;
  final CategoryService categoryService;

  @Transactional
  public Optional<Post> editPost(UUID postId, PostCreationRequest postCreationRequest) {
    postValidationService.validatePost(postCreationRequest);

    var userDTO = SecurityUtils.getUserDTOOrThrow();

    var actualPostCreationDTO = PostUtils.getActualDTO(postCreationRequest);

    var post = postValidationService.getPost(postId);

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
