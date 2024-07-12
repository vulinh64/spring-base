package com.vulinh.data.repository;

import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.entity.PostRevision;
import com.vulinh.data.entity.PostRevisionId;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PostRevisionRepository extends BaseRepository<PostRevision, PostRevisionId> {

  @Query(
      """
      select new com.vulinh.data.dto.post.PostRevisionDTO
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
      pr.revisionCreatedDate,
      pr.revisionCreatedBy)
      from PostRevision pr
      inner join Post p
      on pr.id.postId = p.id
      where p.id = :postId
      """)
  Page<PostRevisionDTO> findByPostIdOrPostSlug(UUID postId, Pageable pageable);
}
