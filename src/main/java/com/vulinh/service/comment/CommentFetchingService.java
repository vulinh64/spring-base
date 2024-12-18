package com.vulinh.service.comment;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.data.dto.comment.CommentDTO;
import com.vulinh.data.entity.QComment;
import com.vulinh.data.entity.QCommentRevision;
import com.vulinh.data.entity.RevisionType;
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

  private JPAQueryFactory queryFactory;

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
    var qCommentCreatedDate = qComment.createdDate;
    var qCommentRevision = QCommentRevision.commentRevision;
    var qCommentAuthor = qComment.createdBy;
    var qCommentId = qComment.id;

    var select =
        Projections.constructor(
            CommentDTO.class,
            qCommentId,
            qComment.content,
            qCommentCreatedDate,
            qComment.updatedDate,
            qCommentAuthor.username,
            qCommentAuthor.fullName,
            new CaseBuilder()
                .when(
                    getQueryFactory()
                        .selectFrom(qCommentRevision)
                        .where(
                            qCommentRevision.id.commentId.eq(qCommentId),
                            qCommentRevision.revisionType.eq(RevisionType.UPDATED))
                        .exists())
                .then(true)
                .otherwise(false));

    return buildBasicQuery(postId, select)
        .orderBy(qCommentCreatedDate.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize());
  }

  private <T> JPAQuery<T> buildBasicQuery(UUID postId, Expression<T> select) {
    var eComment = QComment.comment;

    return getQueryFactory().selectFrom(eComment).select(select).where(eComment.postId.eq(postId));
  }

  private JPAQueryFactory getQueryFactory() {
    if (queryFactory == null) {
      queryFactory = new JPAQueryFactory(entityManager);
    }

    return queryFactory;
  }
}
