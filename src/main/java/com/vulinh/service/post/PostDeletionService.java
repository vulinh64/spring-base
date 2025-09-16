package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.entity.Post;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeletionService {

  private final PostRepository postRepository;

  private final PostValidationService postValidationService;

  @Transactional
  public Optional<Post> deletePost(UUID postId) {

    return postRepository
        .findById(postId)
        .map(
            post -> {
              postValidationService.validateModifyingPermission(
                  SecurityUtils.getUserDTOOrThrow(), post);

              postRepository.delete(post);

              return post;
            });
  }
}
