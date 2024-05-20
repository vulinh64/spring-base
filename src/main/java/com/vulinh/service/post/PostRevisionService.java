package com.vulinh.service.post;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.PostRevisionRepository;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRevisionService {

  private static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  private final PostRepository postRepository;
  private final PostRevisionRepository postRevisionRepository;

  private final PostValidationService postValidationService;
  private final CategoryService categoryService;
  private final TagService tagService;

  @PersistenceContext private final EntityManager entityManager;

  @Transactional
  public Page<PostRevisionDTO> getPostRevisions(String identity, Pageable pageable) {
    throwIfPostNotExists(identity);

    var actualPageable =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Order.desc(PostRevision_.REVISION_CREATED_DATE)));

    return postRevisionRepository.findByPostIdOrPostSlug(identity, actualPageable);
  }

  @Transactional
  public boolean applyRevision(
      String identity, long revisionNumber, HttpServletRequest httpServletRequest) {
    var post =
        postRepository
            .findBy(
                checkPostRevisionJPAQuery(identity).exists(),
                FluentQuery.FetchableFluentQuery::first)
            .orElseThrow(
                () ->
                    ExceptionBuilder.entityNotFound(
                        "Post with ID %s or slug %s not found".formatted(identity, identity),
                        CommonConstant.POST_ENTITY));

    postValidationService.validateModifyingPermission(
        SecurityUtils.getUserDTOOrThrow(httpServletRequest), post);

    var targetRevisionId = PostRevisionId.of(identity, revisionNumber);

    var currentRevisionNumber =
        postRevisionJoinJPAQuery()
            .select(QPostRevision.postRevision.id.revisionNumber.max())
            .fetchFirst();

    if (Optional.ofNullable(currentRevisionNumber).orElse(0L) == revisionNumber) {
      // No need for applying latest revision
      return false;
    }

    return postRevisionRepository
        .findById(targetRevisionId)
        .map(postRevision -> applyRevisionInternal(postRevision, post))
        .orElseThrow(
            () ->
                ExceptionBuilder.entityNotFound(
                    "Post revision number %s for post ID %s not existed"
                        .formatted(revisionNumber, identity),
                    "Post Revision"));
  }

  @Transactional
  public void createPostCreationRevision(Post post) {
    createRevisionInternal(post, RevisionType.CREATED);
  }

  @Transactional
  public boolean createPostEditRevision(Post post) {
    createRevisionInternal(post, RevisionType.UPDATED);

    return true;
  }

  @Transactional
  public boolean createPostDeletionRevision(Post post) {
    createRevisionInternal(post, RevisionType.DELETED);

    return true;
  }

  private void createRevisionInternal(Post post, RevisionType revisionType) {
    postRevisionRepository.save(POST_MAPPER.toPostRevision(post, revisionType));
  }

  private boolean applyRevisionInternal(PostRevision postRevision, Post post) {
    var category = categoryService.getCategory(postRevision.getCategoryId());

    var tags = tagService.parseTags(postRevision.getTags());

    postRepository.save(POST_MAPPER.applyRevision(postRevision, category, tags, post));

    createRevisionInternal(post, RevisionType.UPDATED);

    postRevisionRepository.deleteById(postRevision.getId());

    return true;
  }

  private void throwIfPostNotExists(String identity) {
    var predicate = checkPostRevisionJPAQuery(identity).notExists();

    if (postRevisionRepository.exists(predicate)) {
      throw ExceptionBuilder.entityNotFound(
          "Post with ID %s or slug %s not found".formatted(identity, identity),
          CommonConstant.POST_ENTITY);
    }
  }

  private JPAQuery<Post> checkPostRevisionJPAQuery(String identity) {
    var postEntry = QPost.post;
    var postIdEntry = postEntry.id;

    return postRevisionJoinJPAQuery()
        .where(postIdEntry.eq(identity).or(postEntry.slug.eq(identity)));
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
