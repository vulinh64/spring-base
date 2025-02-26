package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.comment.CommentDTO;
import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
  GenericResponse<Page<CommentDTO>> fetchComments(@PathVariable UUID postId, Pageable pageable);

  @PostMapping("/{postId}")
  @ResponseStatus(HttpStatus.CREATED)
  GenericResponse<Map<String, UUID>> addComment(
      @PathVariable UUID postId,
      @RequestBody NewCommentReplyDTO newCommentReplyDTO,
      HttpServletRequest request);

  @PatchMapping("/{commentId}")
  void editComment(
      @RequestBody NewCommentReplyDTO newCommentReplyDTO,
      @PathVariable UUID commentId,
      HttpServletRequest request);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/{commentId}/reply")
  void createReply(
      @RequestBody NewCommentReplyDTO newCommentReplyDTO,
      @PathVariable UUID commentId,
      HttpServletRequest httpServletRequest);
}
