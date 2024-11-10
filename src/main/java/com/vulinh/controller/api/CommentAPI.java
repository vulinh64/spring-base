package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.comment.CommentDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(EndpointConstant.ENDPOINT_COMMENT)
@Tag(name = "Comment controller", description = "API for managing comments belong to certain posts")
public interface CommentAPI {

  @GetMapping("/{postId}")
  GenericResponse<Page<CommentDTO>> fetchComments(@PathVariable UUID postId, Pageable pageable);
}
