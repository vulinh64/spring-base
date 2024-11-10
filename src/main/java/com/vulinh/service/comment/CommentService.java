package com.vulinh.service.comment;

import com.vulinh.data.dto.comment.NewCommentDTO;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.factory.ExceptionFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  private final NewCommentValidationService newCommentValidationService;
  private final CommentRevisionService commentRevisionService;

  @Transactional
  public void addComment(UUID postId, NewCommentDTO newCommentDTO) {
    newCommentValidationService.validate(newCommentDTO);

    if (!postRepository.existsById(postId)) {
      throw ExceptionFactory.INSTANCE.postNotFound(postId);
    }

    var persistedComment =
        commentRepository.save(CommentMapper.INSTANCE.fromNewComment(newCommentDTO, postId));

    commentRevisionService.createNewCommentRevision(persistedComment);
  }
}
