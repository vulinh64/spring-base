package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.dto.projection.PostRevisionProjection;
import com.vulinh.data.entity.PostRevision;
import com.vulinh.data.entity.ids.PostRevisionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PostRevisionRepository extends org.springframework.data.jpa.repository.JpaRepository<PostRevision, PostRevisionId>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<PostRevision>, org.springframework.data.querydsl.ListQuerydslPredicateExecutor<PostRevision>, org.springframework.data.repository.query.QueryByExampleExecutor<PostRevision> {

  @Query(
        """
        select new com.vulinh.data.dto.projection.PostRevisionProjection
        (pr.id.postId,
        pr.id.revisionNumber,
        pr.revisionType,
        pr.title,
        pr.slug,
        pr.excerpt,
        left(pr.postContent,50)||'...',
        pr.authorId,
        pr.categoryId,
        pr.tags,
        pr.revisionCreatedDateTime,
        pr.revisionCreatedBy)
        from PostRevision pr inner join Post p on pr.id.postId = p.id
        where p.id = :postId
        """)
  Page<PostRevisionProjection> findByPostIdOrPostSlug(UUID postId, Pageable pageable);
}
