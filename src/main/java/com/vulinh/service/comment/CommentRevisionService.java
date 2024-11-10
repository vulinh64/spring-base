package com.vulinh.service.comment;

import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentRevisionService {

  private final CommentRevisionRepository commentRevisionRepository;

  @Transactional
  public void createNewCommentRevision(Comment persistedComment) {
    commentRevisionRepository.save(
        CommentMapper.INSTANCE.fromComment(persistedComment, RevisionType.CREATED));
  }
}
