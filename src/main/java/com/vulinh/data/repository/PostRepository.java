package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends org.springframework.data.jpa.repository.JpaRepository<Post, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Post>, org.springframework.data.querydsl.ListQuerydslPredicateExecutor<Post>, org.springframework.data.repository.query.QueryByExampleExecutor<Post> {

  @Query(
        """
        select
        p.id as id, p.title as title,
        p.excerpt as excerpt,
        p.slug as slug,
        p.createdDateTime as createdDate,
        p.updatedDateTime as updatedDate,
        p.authorId as authorId,
        p.category as category
        from Post p
        """)
  // Cannot fetch tags without combining same post entities, for now
  Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable);
}
