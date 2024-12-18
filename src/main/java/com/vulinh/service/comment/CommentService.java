package com.vulinh.service.comment;

import com.vulinh.data.dto.comment.NewCommentDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final UserRepository userRepository;
  private final CommentRepository commentRepository;

  private final NewCommentValidationService commentValidationService;
  private final CommentRevisionService commentRevisionService;

  @Transactional
  public UUID addComment(UUID postId, NewCommentDTO newCommentDTO, HttpServletRequest request) {
    commentValidationService.validateCreateComment(newCommentDTO, postId);

    var createdBy =
        SecurityUtils.getUserDTO(request)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);

    var persistedComment =
        commentRepository.save(
            CommentMapper.INSTANCE.fromNewComment(newCommentDTO, createdBy, postId));

    commentRevisionService.createNewCommentRevision(persistedComment, RevisionType.CREATED);

    return persistedComment.getId();
  }

  public void editComment(
      UUID postId, UUID commentId, NewCommentDTO newCommentDTO, HttpServletRequest request) {
    var comment =
        commentValidationService.validateEditComment(newCommentDTO, postId, commentId, request);

    var newComment = commentRepository.save(comment.withContent(newCommentDTO.content()));

    commentRevisionService.createNewCommentRevision(newComment, RevisionType.UPDATED);
  }
}
