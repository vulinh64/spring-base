package com.vulinh.service.post.create;

import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.PostUtils;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCreationService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private final PostValidationService postValidationService;
  private final TagService tagService;

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final CategoryService categoryService;

  @Transactional
  public Post createPost(PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    postValidationService.validatePost(postCreationDTO);

    var actualCreationDTO = PostUtils.getActualDTO(postCreationDTO);

    var author =
        SecurityUtils.getUserDTO(httpServletRequest)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findById)
            .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);

    var categoryId = postCreationDTO.categoryId();

    var category = categoryService.getCategory(categoryId);

    var tags = tagService.parseTags(postCreationDTO);

    return postRepository.save(POST_MAPPER.toEntity(actualCreationDTO, author, category, tags));
  }
}
