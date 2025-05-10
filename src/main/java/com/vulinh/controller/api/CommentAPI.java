package com.vulinh.controller.api;

import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.SingleCommentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_COMMENT)
@Tag(name = "Comment controller", description = "API for managing comments belong to certain posts")
public interface CommentAPI {

  @GetMapping("/{postId}")
  GenericResponse<Page<SingleCommentResponse>> fetchComments(
      @PathVariable UUID postId, Pageable pageable);

  @PostMapping("/{postId}")
  @ResponseStatus(HttpStatus.CREATED)
  GenericResponse<Map<String, UUID>> addComment(
      @PathVariable UUID postId, @RequestBody NewCommentRequest newCommentRequest);

  @PatchMapping("/{commentId}")
  void editComment(@RequestBody NewCommentRequest newCommentRequest, @PathVariable UUID commentId);
}
