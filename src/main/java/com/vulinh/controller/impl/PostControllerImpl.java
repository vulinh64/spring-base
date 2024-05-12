package com.vulinh.controller.impl;

import com.vulinh.controller.PostController;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.dto.post.PostDTO;
import com.vulinh.data.dto.post.SinglePostDTO;
import com.vulinh.data.projection.PrefetchPostProjection;
import com.vulinh.service.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostControllerImpl implements PostController {

  private final PostService postService;

  @Override
  public GenericResponse<Page<PrefetchPostProjection>> getPostsByUser(
      Pageable pageable, HttpServletRequest httpServletRequest) {
    return GenericResponse.success(postService.getPostsByCurrentUser(pageable, httpServletRequest));
  }

  @Override
  public GenericResponse<SinglePostDTO> getSinglePost(
      String identity, HttpServletRequest httpServletRequest) {
    return GenericResponse.success(postService.getSinglePost(identity, httpServletRequest));
  }

  @Override
  public GenericResponse<PostDTO> createPost(
      PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    return GenericResponse.success(postService.createPost(postCreationDTO, httpServletRequest));
  }
}
