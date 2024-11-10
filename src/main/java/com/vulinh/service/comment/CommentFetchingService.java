package com.vulinh.service.comment;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.data.dto.comment.CommentDTO;
import com.vulinh.data.entity.QComment;
import com.vulinh.data.entity.QUsers;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.factory.ExceptionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentFetchingService {

  @PersistenceContext private final EntityManager entityManager;

  private final PostRepository postRepository;

  public Page<CommentDTO> fetchComments(UUID postId, Pageable pageable) {
    if (!postRepository.existsById(postId)) {
      throw ExceptionFactory.INSTANCE.postNotFound(postId);
    }

    return PageableExecutionUtils.getPage(
        buildFetchQuery(postId, pageable).fetch(), pageable, buildCountQuery(postId)::fetchOne);
  }

  private JPAQuery<Long> buildCountQuery(UUID postId) {
    return buildBasicQuery(postId, QComment.comment.count());
  }

  private JPAQuery<CommentDTO> buildFetchQuery(UUID postId, Pageable pageable) {
    var qComment = QComment.comment;
    var qUsers = QUsers.users;
    var qCommentCreatedDate = qComment.createdDate;

    var select =
        Projections.constructor(
            CommentDTO.class,
            qComment.id,
            qComment.content,
            qCommentCreatedDate,
            qComment.updatedDate,
            qUsers.username,
            qUsers.fullName);

    return buildBasicQuery(postId, select)
        .orderBy(qCommentCreatedDate.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());
  }

  private <T> JPAQuery<T> buildBasicQuery(UUID postId, Expression<T> select) {
    var eComment = QComment.comment;
    var eUsers = QUsers.users;

    return new JPAQueryFactory(entityManager)
        .selectFrom(eComment)
        .select(select)
        .join(eUsers)
        .on(eUsers.id.eq(eComment.createdBy))
        .where(eComment.postId.eq(postId));
  }
}
