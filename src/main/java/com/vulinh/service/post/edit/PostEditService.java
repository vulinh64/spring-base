package com.vulinh.service.post.edit;

import com.vulinh.data.dto.post.PostCreationDTO;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostEditService {

  private final PostRepository postRepository;

  private final PostValidationService postValidationService;
  private final PostEditValidationService postEditValidationService;

  public boolean editPost(
      String postId, PostCreationDTO postCreationDTO, HttpServletRequest httpServletRequest) {
    postValidationService.validatePost(postCreationDTO);

    var userDTO = SecurityUtils.getUserDTOOrThrow(httpServletRequest);

    var post = postRepository.findByIdOrFailed(postId);

    postEditValidationService.validateEditPermission(postCreationDTO, userDTO, post);

    // TODO: Tomorrow
    return false;
  }
}
