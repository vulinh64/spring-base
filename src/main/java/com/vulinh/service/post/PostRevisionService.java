package com.vulinh.service.post;

import module java.base;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.response.PostRevisionResponse;
import com.vulinh.data.entity.*;
import com.vulinh.data.entity.ids.PostRevisionId;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.data.repository.PostRevisionRepository;
import com.vulinh.exception.NotFoundException;
import com.vulinh.factory.PostFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.category.CategoryService;
import com.vulinh.service.tag.TagService;
import com.vulinh.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
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

  static final PostMapper POST_MAPPER = PostMapper.INSTANCE;

  final PostRepository postRepository;
  final PostRevisionRepository postRevisionRepository;

  final PostValidationService postValidationService;
  final CategoryService categoryService;
  final TagService tagService;

  @PersistenceContext final EntityManager entityManager;

  @Transactional
  public Page<PostRevisionResponse> getPostRevisions(UUID postId, Pageable pageable) {

    if (postRevisionRepository.exists(checkPostRevisionJPAQuery(postId).notExists())) {
      throw NotFoundException.entityNotFound(
          CommonConstant.POST_ENTITY, postId, ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID);
    }

    var actualPageable =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(
                Order.desc(
                    QPostRevision.postRevision.revisionCreatedDate.getMetadata().getName())));

    return postRevisionRepository
        .findByPostIdOrPostSlug(postId, actualPageable)
        .map(POST_MAPPER::toPostRevisionResponse);
  }

  @Transactional
  public boolean applyRevision(UUID postId, long revisionNumber) {
    var post =
        postRepository
            .findBy(
                checkPostRevisionJPAQuery(postId).exists(), FluentQuery.FetchableFluentQuery::first)
            .orElseThrow(
                () ->
                    NotFoundException.entityNotFound(
                        CommonConstant.POST_ENTITY,
                        postId,
                        ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));

    postValidationService.validateModifyingPermission(SecurityUtils.getUserDTOOrThrow(), post);

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
                NotFoundException.entityNotFound(
                    CommonConstant.POST_REVISION_ENTITY,
                    PostRevisionId.of(postId, revisionNumber),
                    ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));
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

  void createRevisionInternal(Post post, RevisionType revisionType) {
    postRevisionRepository.save(POST_MAPPER.toPostRevision(post, revisionType));
  }

  boolean applyRevisionInternal(PostRevision postRevision, Post post) {
    var category =
        Objects.equals(postRevision.getCategoryId(), post.getCategory().getId())
            ? null
            : categoryService.getCategory(postRevision.getCategoryId());

    var tags = tagService.parseTags(postRevision.getTags());

    postRepository.save(POST_MAPPER.applyRevision(postRevision, category, tags, post));

    createRevisionInternal(post, RevisionType.UPDATED);

    postRevisionRepository.deleteById(postRevision.getId());

    return true;
  }

  JPAQuery<Post> checkPostRevisionJPAQuery(UUID identity) {
    return postRevisionJoinJPAQuery().where(QPost.post.id.eq(identity));
  }

  JPAQuery<Post> postRevisionJoinJPAQuery() {
    var postEntry = QPost.post;
    var postRevisionEntry = QPostRevision.postRevision;

    return new JPAQueryFactory(entityManager)
        .selectFrom(postEntry)
        .join(postRevisionEntry)
        .on(postEntry.id.eq(postRevisionEntry.id.postId));
  }
}
