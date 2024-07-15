package com.vulinh.service.post;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.projection.PrefetchPostProjection;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.post.create.PostCreationService;
import com.vulinh.service.post.edit.PostDeletionService;
import com.vulinh.service.post.edit.PostEditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class PostService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private final PostRepository postRepository;

  private final PostCreationService postCreationService;
  private final PostEditService postEditService;
  private final PostDeletionService postDeletionService;
  private final PostRevisionService postRevisionService;

  public Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable) {
    return postRepository.findPrefetchPosts(pageable);
  }

  public SinglePostDTO getSinglePost(UUID postId) {
    return postRepository
        .findById(postId)
        .map(POST_MAPPER::toSinglePostDTO)
        .orElseThrow(
            () ->
                ExceptionBuilder.entityNotFound(
                    "Post with either id or slug [%s] not found".formatted(postId),
                    CommonConstant.POST_ENTITY));
  }

  @Transactional
  public PostDTO createPost(
      PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    // Delegate to PostCreationService
    var entity = postCreationService.createPost(postCreationDTO, httpServletRequest);

    // Delegate to PostRevisionService
    postRevisionService.createPostCreationRevision(entity);

    return POST_MAPPER.toDto(entity);
  }

  @Transactional
  public boolean editPost(
      UUID postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    return postEditService
        .editPost(postId, postCreationDTO, httpServletRequest)
        .map(postRevisionService::createPostEditRevision)
        .isPresent();
  }

  @Transactional
  public boolean deletePost(UUID postId, HttpServletRequest httpServletRequest) {
    return postDeletionService
        .deletePost(postId, httpServletRequest)
        .map(postRevisionService::createPostDeletionRevision)
        .isPresent();
  }
}
