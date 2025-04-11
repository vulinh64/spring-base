package com.vulinh.controller.impl;

import com.vulinh.controller.api.CommentAPI;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.SingleCommentResponse;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.comment.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentAPI {

  public static final GenericResponseFactory GENERIC_RESPONSE_FACTORY =
      GenericResponseFactory.INSTANCE;

  private final CommentService commentService;

  @Override
  public GenericResponse<Page<SingleCommentResponse>> fetchComments(
      UUID postId, Pageable pageable) {
    return GENERIC_RESPONSE_FACTORY.success(commentService.fetchComments(postId, pageable));
  }

  @Override
  public GenericResponse<Map<String, UUID>> addComment(
      UUID postId, NewCommentRequest newCommentRequest, HttpServletRequest request) {
    var uuid = commentService.addComment(postId, newCommentRequest, request);

    return GENERIC_RESPONSE_FACTORY.success(Map.of("commentId", uuid));
  }

  @Override
  public void editComment(
      NewCommentRequest newCommentRequest, UUID commentId, HttpServletRequest request) {
    commentService.editComment(newCommentRequest, commentId, request);
  }
}
