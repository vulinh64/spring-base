package com.vulinh.service.post;

import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.post.PostUtils;
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
  public Post createPost(PostCreationRequest postCreationRequest) {
    postValidationService.validatePost(postCreationRequest);

    var actualCreationDTO = PostUtils.getActualDTO(postCreationRequest);

    var author =
        SecurityUtils.getUserDTO()
            .map(UserBasicResponse::id)
            .flatMap(userRepository::findById)
            .orElseThrow(AuthorizationException::invalidAuthorization);

    var categoryId = postCreationRequest.categoryId();

    var category = categoryService.getCategory(categoryId);

    var tags = tagService.parseTags(postCreationRequest);

    return postRepository.save(POST_MAPPER.toEntity(actualCreationDTO, author, category, tags));
  }
}
