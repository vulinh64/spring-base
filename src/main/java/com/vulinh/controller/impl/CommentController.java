package com.vulinh.controller.impl;

import com.vulinh.controller.api.CommentAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.comment.CommentDTO;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.comment.CommentFetchingService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentAPI {

  private final CommentFetchingService commentFetchingService;

  @Override
  public GenericResponse<Page<CommentDTO>> fetchComments(UUID postId, Pageable pageable) {
    return GenericResponseFactory.INSTANCE.success(commentFetchingService.fetchComments(postId, pageable));
  }
}
