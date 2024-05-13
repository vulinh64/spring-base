package com.vulinh.service.post.edit;

import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.PostUtils;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostEditService {

  private final PostRepository postRepository;

  private final PostValidationService postValidationService;
  private final PostEditValidationService postEditValidationService;
  private final TagService tagService;
  private final CategoryService categoryService;

  @Transactional
  public Optional<Post> editPost(
      String postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    postValidationService.validatePost(postCreationDTO);

    var userDTO = SecurityUtils.getUserDTOOrThrow(httpServletRequest);

    var actualPostCreationDTO = PostUtils.getActualPostEditDTO(postCreationDTO);

    var post = postRepository.findByIdOrFailed(postId);

    postEditValidationService.validateEditPermission(actualPostCreationDTO, userDTO, post);

    if (postEditValidationService.isPostUnchanged(actualPostCreationDTO, post)) {
      // Post unchanged
      return Optional.empty();
    }

    var category = categoryService.getCategory(postCreationDTO.categoryId());

    var tags = tagService.parseTags(actualPostCreationDTO);

    PostMapper.INSTANCE.merge(actualPostCreationDTO, category, tags, post);

    postRepository.save(post);

    return Optional.of(post);
  }
}
