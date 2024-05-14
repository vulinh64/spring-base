package com.vulinh.data.repository;

import com.vulinh.data.entity.Post;
import com.vulinh.data.projection.PrefetchPostProjection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends BaseRepository<Post, String> {

  @Query(
      """
      select
      p.id as id,
      p.title as title,
      p.excerpt as excerpt,
      p.slug as slug,
      p.createdDate as createdDate,
      p.updatedDate as updatedDate,
      p.author as author,
      p.category as category
      from Post p
      """)
  // Cannot fetch tags without combining same post entities, for now
  Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable);

  @Query("select p from Post p where p.id = :identity or p.slug = :identity")
  Optional<Post> findSinglePost(String identity);
}
