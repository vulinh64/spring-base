package com.vulinh.service.comment;

import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.SingleCommentResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final UserRepository userRepository;
  private final CommentRepository commentRepository;

  private final NewCommentValidationService commentValidationService;
  private final CommentRevisionService commentRevisionService;
  private final CommentFetchingService commentFetchingService;

  @Transactional
  public UUID addComment(
      UUID postId, NewCommentRequest newCommentRequest, HttpServletRequest request) {
    commentValidationService.validate(newCommentRequest);

    var createdBy =
        SecurityUtils.getUserDTO(request)
            .map(UserBasicResponse::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);

    var persistedComment =
        commentRepository.save(
            CommentMapper.INSTANCE.fromNewComment(newCommentRequest, createdBy, postId));

    commentRevisionService.createNewCommentRevision(persistedComment, RevisionType.CREATED);

    return persistedComment.getId();
  }

  public void editComment(
      NewCommentRequest newCommentRequest, UUID commentId, HttpServletRequest request) {
    var comment =
        commentValidationService.validateEditComment(newCommentRequest, commentId, request);

    var newComment = commentRepository.save(comment.withContent(newCommentRequest.content()));

    commentRevisionService.createNewCommentRevision(newComment, RevisionType.UPDATED);
  }

  public Page<SingleCommentResponse> fetchComments(UUID postId, Pageable pageable) {
    return commentFetchingService.fetchComments(postId, pageable);
  }
}
