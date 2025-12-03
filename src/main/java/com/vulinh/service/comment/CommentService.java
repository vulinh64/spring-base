package com.vulinh.service.comment;

import module java.base;

import com.vulinh.data.dto.request.NewCommentRequest;
import com.vulinh.data.dto.response.CommentResponse;
import com.vulinh.data.dto.response.SingleCommentResponse;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.service.event.EventService;
import com.vulinh.service.post.PostValidationService;
import com.vulinh.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  final CommentRepository commentRepository;

  final PostValidationService postValidationService;
  final NewCommentValidationService commentValidationService;
  final CommentRevisionService commentRevisionService;
  final CommentFetchingService commentFetchingService;

  final EventService eventService;

  @Transactional
  public CommentResponse addComment(UUID postId, NewCommentRequest newCommentRequest) {
    commentValidationService.validate(newCommentRequest);

    var post = postValidationService.getPost(postId);

    var comment =
        commentRepository.save(CommentMapper.INSTANCE.fromNewComment(newCommentRequest, post));

    var commentRevision =
        commentRevisionService.createNewCommentRevision(comment, RevisionType.CREATED);

    eventService.sendNewCommentEvent(comment, post, SecurityUtils.getUserDTOOrThrow());

    return CommentResponse.of(postId, commentRevision);
  }

  public CommentResponse editComment(NewCommentRequest newCommentRequest, UUID commentId) {
    var comment = commentValidationService.validateEditComment(newCommentRequest, commentId);

    comment.setContent(newCommentRequest.content());

    var newComment = commentRepository.save(comment);

    var newCommentRevision =
        commentRevisionService.createNewCommentRevision(newComment, RevisionType.UPDATED);

    return CommentResponse.of(comment.getPostId(), newCommentRevision);
  }

  public Page<SingleCommentResponse> fetchComments(UUID postId, Pageable pageable) {
    return commentFetchingService.fetchComments(postId, pageable);
  }
}
