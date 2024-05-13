package com.vulinh.service.post.edit;

import com.vulinh.data.entity.Post;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeletionService {

  private final PostRepository postRepository;

  private final PostValidationService postValidationService;

  @Transactional
  public Optional<Post> deletePost(String postId, HttpServletRequest httpServletRequest) {

    return postRepository
        .findById(postId)
        .map(
            post -> {
              postValidationService.validateModifyingPermission(
                  SecurityUtils.getUserDTOOrThrow(httpServletRequest), post);

              postRepository.delete(post);

              return post;
            });
  }
}
