package com.vulinh.service.comment;

import com.vulinh.data.dto.comment.CommentDTO;
import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
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
  public UUID addComment(UUID postId, NewCommentReplyDTO newCommentReplyDTO, HttpServletRequest request) {
    commentValidationService.validate(newCommentReplyDTO);

    var createdBy =
        SecurityUtils.getUserDTO(request)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);

    var persistedComment =
        commentRepository.save(
            CommentMapper.INSTANCE.fromNewCommentReply(newCommentReplyDTO, createdBy, postId));

    commentRevisionService.createNewCommentRevision(persistedComment, RevisionType.CREATED);

    return persistedComment.getId();
  }

  public void editComment(NewCommentReplyDTO newCommentReplyDTO, UUID commentId, HttpServletRequest request) {
    var comment = commentValidationService.validateEditComment(newCommentReplyDTO, commentId, request);

    var newComment = commentRepository.save(comment.withContent(newCommentReplyDTO.content()));

    commentRevisionService.createNewCommentRevision(newComment, RevisionType.UPDATED);
  }

  public Page<CommentDTO> fetchComments(UUID postId, Pageable pageable) {
    return commentFetchingService.fetchComments(postId, pageable);
  }
}
