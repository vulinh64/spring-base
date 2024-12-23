package com.vulinh.service.post;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.PostRevisionRepository;
import com.vulinh.factory.ElasticsearchEventFactory;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.PostFactory;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostRevisionService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private final PostRepository postRepository;
  private final PostRevisionRepository postRevisionRepository;

  private final PostValidationService postValidationService;
  private final CategoryService categoryService;
  private final TagService tagService;

  private final ApplicationEventPublisher applicationEventPublisher;

  @PersistenceContext private final EntityManager entityManager;

  @Transactional
  public Page<PostRevisionDTO> getPostRevisions(UUID postId, Pageable pageable) {
    throwIfPostNotExists(postId);

    var actualPageable =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(
                Order.desc(
                    QPostRevision.postRevision.revisionCreatedDate.getMetadata().getName())));

    return postRevisionRepository.findByPostIdOrPostSlug(postId, actualPageable);
  }

  @Transactional
  public boolean applyRevision(
      UUID postId, long revisionNumber, HttpServletRequest httpServletRequest) {
    var post =
        postRepository
            .findBy(
                checkPostRevisionJPAQuery(postId).exists(), FluentQuery.FetchableFluentQuery::first)
            .orElseThrow(() -> PostValidationService.postOrSlugNotFound(postId));

    postValidationService.validateModifyingPermission(
        SecurityUtils.getUserDTOOrThrow(httpServletRequest), post);

    var currentRevisionNumber =
        postRevisionJoinJPAQuery()
            .select(QPostRevision.postRevision.id.revisionNumber.max())
            .fetchFirst();

    if (Optional.ofNullable(currentRevisionNumber).orElse(0L) == revisionNumber) {
      // No need for applying latest revision
      return false;
    }

    return postRevisionRepository
        .findById(PostFactory.INSTANCE.createRevisionId(postId, revisionNumber))
        .map(postRevision -> applyRevisionInternal(postRevision, post))
        .orElseThrow(
            () ->
                EXCEPTION_FACTORY.entityNotFound(
                    "Post revision number %s for post ID %s not existed"
                        .formatted(revisionNumber, postId),
                    "Post Revision"));
  }

  @Transactional
  public void createPostCreationRevision(Post post) {
    createRevisionInternal(post, RevisionType.CREATED);
  }

  @Transactional
  public void createPostEditRevision(Post post) {
    createRevisionInternal(post, RevisionType.UPDATED);
  }

  @Transactional
  public void createPostDeletionRevision(Post post) {
    createRevisionInternal(post, RevisionType.DELETED);
  }

  private void createRevisionInternal(Post post, RevisionType revisionType) {
    postRevisionRepository.save(POST_MAPPER.toPostRevision(post, revisionType));
  }

  private boolean applyRevisionInternal(PostRevision postRevision, Post post) {
    var category =
        Objects.equals(postRevision.getCategoryId(), post.getCategory().getId())
            ? null
            : categoryService.getCategory(postRevision.getCategoryId());

    var tags = tagService.parseTags(postRevision.getTags());

    postRepository.save(POST_MAPPER.applyRevision(postRevision, category, tags, post));

    createRevisionInternal(post, RevisionType.UPDATED);

    postRevisionRepository.deleteById(postRevision.getId());

    applicationEventPublisher.publishEvent(
        ElasticsearchEventFactory.INSTANCE.ofPersistence(POST_MAPPER.toDocumentedPost(post)));

    return true;
  }

  private void throwIfPostNotExists(UUID postId) {
    var predicate = checkPostRevisionJPAQuery(postId).notExists();

    if (postRevisionRepository.exists(predicate)) {
      throw PostValidationService.postOrSlugNotFound(postId);
    }
  }

  private JPAQuery<Post> checkPostRevisionJPAQuery(UUID identity) {
      return postRevisionJoinJPAQuery().where(QPost.post.id.eq(identity));
  }

  private JPAQuery<Post> postRevisionJoinJPAQuery() {
    var postEntry = QPost.post;
    var postRevisionEntry = QPostRevision.postRevision;

    return new JPAQueryFactory(entityManager)
        .selectFrom(postEntry)
        .join(postRevisionEntry)
        .on(postEntry.id.eq(postRevisionEntry.id.postId));
  }
}
