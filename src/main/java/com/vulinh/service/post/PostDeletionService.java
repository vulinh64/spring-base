package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.entity.Post;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.NotFound404Exception;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostDeletionService {

  final PostRepository postRepository;

  final PostValidationService postValidationService;

  @Transactional
  public Optional<Post> deletePost(UUID postId) {
    try {
      var post = postValidationService.getPost(postId);

      postValidationService.validateModifyingPermission(SecurityUtils.getUserDTOOrThrow(), post);

      postRepository.delete(post);

      return Optional.of(post);
    } catch (NotFound404Exception notFound404Exception) {
      return Optional.empty();
    }
  }
}
