package com.vulinh.service.post;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.BasicPostResponse;
import com.vulinh.data.dto.response.SinglePostResponse;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.exception.NotFoundException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  final PostRepository postRepository;

  final PostCreationService postCreationService;
  final PostEditService postEditService;
  final PostDeletionService postDeletionService;
  final PostRevisionService postRevisionService;
  final EventService eventService;

  public Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable) {
    return postRepository.findPrefetchPosts(pageable);
  }

  public SinglePostResponse getSinglePost(UUID postId) {
    return postRepository
        .findById(postId)
        .map(POST_MAPPER::toSinglePostDTO)
        .orElseThrow(
            () ->
                NotFoundException.entityNotFound(
                    CommonConstant.POST_ENTITY,
                    postId,
                    ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));
  }

  @Transactional
  public BasicPostResponse createPost(PostCreationRequest postCreationRequest) {
    // Delegate to PostCreationService
    var newPost = postCreationService.createPost(postCreationRequest);

    // Delegate to PostRevisionService
    postRevisionService.createPostCreationRevision(newPost);

    // Delegate to EventService
    eventService.sendNewPostEvent(newPost);

    return POST_MAPPER.toDto(newPost);
  }

  @Transactional
  public boolean editPost(UUID postId, PostCreationRequest postCreationRequest) {
    var possiblePost = postEditService.editPost(postId, postCreationRequest);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostEditRevision(post);

      return true;
    }

    return false;
  }

  @Transactional
  public boolean deletePost(UUID postId) {
    var possiblePost = postDeletionService.deletePost(postId);

    if (possiblePost.isPresent()) {
      var post = possiblePost.get();

      postRevisionService.createPostDeletionRevision(post);

      return true;
    }

    return false;
  }
}
