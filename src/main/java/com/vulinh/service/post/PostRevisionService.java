package com.vulinh.service.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.PostMapper;
import com.vulinh.data.repository.PostRevisionRepository;
import com.vulinh.exception.CommonException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostRevisionService {

  private final PostRevisionRepository postRevisionRepository;

  @PersistenceContext private final EntityManager entityManager;

  @Transactional
  public Page<PostRevisionDTO> getPostRevisions(String identity, Pageable pageable) {
    throwsIfPostNotExists(identity);

    var actualPageable =
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Order.desc(PostRevision_.REVISION_CREATED_DATE)));

    return postRevisionRepository.findByPostIdOrPostSlug(identity, actualPageable);
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
    postRevisionRepository.save(PostMapper.INSTANCE.toPostRevision(post, revisionType));
  }

  private void throwsIfPostNotExists(String identity) {
    var postEntry = QPost.post;
    var postIdEntry = postEntry.id;
    var postRevisionEntry = QPostRevision.postRevision;

    var predicate =
        new JPAQueryFactory(entityManager)
            .selectFrom(postEntry)
            .join(postRevisionEntry)
            .on(postIdEntry.eq(postRevisionEntry.id.postId))
            .where(postIdEntry.eq(identity).or(postEntry.slug.eq(identity)))
            .notExists();

    if (postRevisionRepository.exists(predicate)) {
      throw new CommonException(
          "Post with ID %s or slug %s not found".formatted(identity, identity),
          CommonMessage.MESSAGE_INVALID_ENTITY_ID,
          CommonConstant.POST_ENTITY);
    }
  }
}
