package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.constant.EndpointConstant.PostEndpoint;
import com.vulinh.data.dto.elasticsearch.ESimplePost;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.projection.PrefetchPostProjection;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_POST)
@Tag(name = "Post controller", description = "Manage posts")
public interface PostAPI {

  @GetMapping
  GenericResponse<Page<PrefetchPostProjection>> findPrefetchPosts(
      Pageable pageable, HttpServletRequest httpServletRequest);

  @GetMapping("/quick-search")
  GenericResponse<Page<ESimplePost>> quickSearchPosts(String keyword, Pageable pageable);

  @GetMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT)
  GenericResponse<SinglePostDTO> getSinglePost(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId,
      HttpServletRequest httpServletRequest);

  @GetMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT + PostEndpoint.REVISION_ENDPOINT)
  GenericResponse<Page<PostRevisionDTO>> getPostRevisions(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId, Pageable pageable);

  @PostMapping
  GenericResponse<PostDTO> createPost(
      @RequestBody PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest);

  @PatchMapping(PostEndpoint.POST_ID_VARIABLE_FORMAT)
  ResponseEntity<Void> editPost(
      @PathVariable(PostEndpoint.POST_ID_VARIABLE) UUID postId,
      @RequestBody PostCreationDTO postCreationDTO,
      HttpServletRequest httpServletRequest);

  @DeleteMapping(PostEndpoint.POST_ID_VARIABLE_FORMAT)
  ResponseEntity<Void> deletePost(
      @PathVariable(PostEndpoint.POST_ID_VARIABLE) UUID postId,
      HttpServletRequest httpServletRequest);

  @PatchMapping(PostEndpoint.IDENTITY_VARIABLE_FORMAT + PostEndpoint.REVISION_ENDPOINT)
  ResponseEntity<Void> applyRevision(
      @PathVariable(PostEndpoint.IDENTITY_VARIABLE) UUID postId,
      @RequestParam long revisionNumber,
      HttpServletRequest httpServletRequest);
}
