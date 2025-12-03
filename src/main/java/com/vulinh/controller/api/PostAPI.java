package com.vulinh.controller.api;

import module java.base;

import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.PostEndpoint;
import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.dto.request.PostCreationRequest;
import com.vulinh.data.dto.response.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_POST)
@Tag(name = "Post controller", description = "Manage posts")
public interface PostAPI {

  @GetMapping
  GenericResponse<Page<PrefetchPostProjection>> findPrefetchPosts(Pageable pageable);

  @GetMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT)
  GenericResponse<SinglePostResponse> getSinglePost(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId);

  @GetMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT + PostEndpoint.REVISION_ENDPOINT)
  GenericResponse<Page<PostRevisionResponse>> getPostRevisions(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId, Pageable pageable);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  GenericResponse<BasicPostResponse> createPost(
      @RequestBody PostCreationRequest postCreationRequest);

  @PatchMapping(PostEndpoint.POST_ID_VARIABLE_FORMAT)
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  ResponseEntity<Void> editPost(
      @PathVariable(PostEndpoint.POST_ID_VARIABLE) UUID postId,
      @RequestBody PostCreationRequest postCreationRequest);

  @DeleteMapping(PostEndpoint.POST_ID_VARIABLE_FORMAT)
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  ResponseEntity<Void> deletePost(@PathVariable(PostEndpoint.POST_ID_VARIABLE) UUID postId);

  @PatchMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT + PostEndpoint.REVISION_ENDPOINT)
  @SecurityRequirement(name = CommonConstant.SECURITY_SCHEME_NAME)
  ResponseEntity<Void> applyRevision(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId, @RequestParam long revisionNumber);
}
