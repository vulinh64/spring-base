package com.vulinh.controller.impl;

import com.vulinh.controller.api.PostAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.projection.PrefetchPostProjection;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.post.PostRevisionService;
import com.vulinh.service.post.PostService;
import com.vulinh.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PostController implements PostAPI {

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  private final PostService postService;
  private final PostRevisionService postRevisionService;

  @Override
  public GenericResponse<Page<PrefetchPostProjection>> findPrefetchPosts(
      Pageable pageable, HttpServletRequest httpServletRequest) {
    return RESPONSE_FACTORY.success(postService.findPrefetchPosts(pageable));
  }

  @Override
  public GenericResponse<SinglePostDTO> getSinglePost(
          UUID postId, HttpServletRequest httpServletRequest) {
    return RESPONSE_FACTORY.success(postService.getSinglePost(postId));
  }

  @Override
  public GenericResponse<Page<PostRevisionDTO>> getPostRevisions(UUID postId, Pageable pageable) {
    return RESPONSE_FACTORY.success(postRevisionService.getPostRevisions(postId, pageable));
  }

  @Override
  public GenericResponse<PostDTO> createPost(
      PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    return RESPONSE_FACTORY.success(postService.createPost(postCreationDTO, httpServletRequest));
  }

  @Override
  public ResponseEntity<Void> editPost(
      UUID postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    return ResponseUtils.returnOkOrNoContent(
        postService.editPost(postId, postCreationDTO, httpServletRequest));
  }

  @Override
  public ResponseEntity<Void> deletePost(UUID postId, HttpServletRequest httpServletRequest) {
    return postService.deletePost(postId, httpServletRequest)
        ? ResponseEntity.ok().build()
        : ResponseEntity.notFound().build();
  }

  @Override
  public ResponseEntity<Void> applyRevision(
      UUID postId, long revisionNumber, HttpServletRequest httpServletRequest) {
    return ResponseUtils.returnOkOrNoContent(
        postRevisionService.applyRevision(postId, revisionNumber, httpServletRequest));
  }
}
