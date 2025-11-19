package com.vulinh.service.comment;

import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.CommentRevision;
import com.vulinh.data.entity.RevisionType;
import com.vulinh.data.mapper.CommentMapper;
import com.vulinh.data.repository.CommentRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentRevisionService {

  final CommentRevisionRepository commentRevisionRepository;

  @Transactional
  public CommentRevision createNewCommentRevision(
      Comment persistedComment, RevisionType revisionType) {
    return commentRevisionRepository.save(
        CommentMapper.INSTANCE.fromComment(persistedComment, revisionType));
  }
}
