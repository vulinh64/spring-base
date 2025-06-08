package com.vulinh.controller.impl;

import com.vulinh.controller.api.PostAPI;
import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.*;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.service.post.PostRevisionService;
import com.vulinh.service.post.PostService;
import com.vulinh.utils.ResponseUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController implements PostAPI {

  private final PostService postService;
  private final PostRevisionService postRevisionService;

  @Override
  public GenericResponse<Page<PrefetchPostProjection>> findPrefetchPosts(Pageable pageable) {
    return ResponseCreator.success(postService.findPrefetchPosts(pageable));
  }

  @Override
  public GenericResponse<SinglePostResponse> getSinglePost(UUID postId) {
    return ResponseCreator.success(postService.getSinglePost(postId));
  }

  @Override
  public GenericResponse<Page<PostRevisionResponse>> getPostRevisions(
      UUID postId, Pageable pageable) {
    return ResponseCreator.success(postRevisionService.getPostRevisions(postId, pageable));
  }

  @Override
  public GenericResponse<BasicPostResponse> createPost(PostCreationRequest postCreationRequest) {
    return ResponseCreator.success(postService.createPost(postCreationRequest));
  }

  @Override
  public ResponseEntity<Void> editPost(UUID postId, PostCreationRequest postCreationRequest) {
    return ResponseUtils.returnOkOrNoContent(postService.editPost(postId, postCreationRequest));
  }

  @Override
  public ResponseEntity<Void> deletePost(UUID postId) {
    return postService.deletePost(postId)
        ? ResponseEntity.ok().build()
        : ResponseEntity.notFound().build();
  }

  @Override
  public ResponseEntity<Void> applyRevision(UUID postId, long revisionNumber) {
    return ResponseUtils.returnOkOrNoContent(
        postRevisionService.applyRevision(postId, revisionNumber));
  }
}
