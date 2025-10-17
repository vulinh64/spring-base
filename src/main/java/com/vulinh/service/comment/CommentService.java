package com.vulinh.service.comment;

import module java.base;

import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.SingleCommentResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  final UserRepository userRepository;
  final CommentRepository commentRepository;

  final NewCommentValidationService commentValidationService;
  final CommentRevisionService commentRevisionService;
  final CommentFetchingService commentFetchingService;

  @Transactional
  public UUID addComment(UUID postId, NewCommentRequest newCommentRequest) {
    commentValidationService.validate(newCommentRequest);

    var createdBy =
        SecurityUtils.getUserDTO()
            .map(UserBasicResponse::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(AuthorizationException::invalidAuthorization);

    var persistedComment =
        commentRepository.save(
            CommentMapper.INSTANCE.fromNewComment(newCommentRequest, createdBy, postId));

    commentRevisionService.createNewCommentRevision(persistedComment, RevisionType.CREATED);

    return persistedComment.getId();
  }

  public void editComment(NewCommentRequest newCommentRequest, UUID commentId) {
    var comment = commentValidationService.validateEditComment(newCommentRequest, commentId);

    var newComment = commentRepository.save(comment.withContent(newCommentRequest.content()));

    commentRevisionService.createNewCommentRevision(newComment, RevisionType.UPDATED);
  }

  public Page<SingleCommentResponse> fetchComments(UUID postId, Pageable pageable) {
    return commentFetchingService.fetchComments(postId, pageable);
  }
}
