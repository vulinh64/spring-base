package com.vulinh.controller;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.projection.PrefetchPostProjection;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_POST)
public interface PostController {

  @GetMapping
  GenericResponse<Page<PrefetchPostProjection>> getPostsByUser(
      Pageable pageable, HttpServletRequest httpServletRequest);

  @GetMapping("/{identity}")
  GenericResponse<SinglePostDTO> getSinglePost(
      @PathVariable("identity") String identity, HttpServletRequest httpServletRequest);

  @PostMapping
  GenericResponse<PostDTO> createPost(
      @RequestBody PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest);

  @PatchMapping("/{postId}")
  ResponseEntity<Void> editPost(
      @PathVariable("postId") String postId,
      @RequestBody PostCreationDTO postCreationDTO,
      HttpServletRequest httpServletRequest);
}
