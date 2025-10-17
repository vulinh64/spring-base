package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.CommentAPI;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
import com.vulinh.data.dto.response.SingleCommentResponse;
import com.vulinh.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentAPI {

  final CommentService commentService;

  @Override
  public GenericResponse<Page<SingleCommentResponse>> fetchComments(
      UUID postId, Pageable pageable) {
    return ResponseCreator.success(commentService.fetchComments(postId, pageable));
  }

  @Override
  public GenericResponse<Map<String, UUID>> addComment(
      UUID postId, NewCommentRequest newCommentRequest) {
    var uuid = commentService.addComment(postId, newCommentRequest);

    return ResponseCreator.success(Map.of("commentId", uuid));
  }

  @Override
  public void editComment(
      NewCommentRequest newCommentRequest, UUID commentId) {
    commentService.editComment(newCommentRequest, commentId);
  }
}
