package com.vulinh.data.repository;

import com.vulinh.constant.NamedQueryConstant;
import com.vulinh.data.entity.Post;
import com.vulinh.data.projection.PrefetchPostProjection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends BaseRepository<Post, UUID> {

  @Query(name = NamedQueryConstant.FIND_PREFETCHED_POSTS)
  // Cannot fetch tags without combining same post entities, for now
  Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable);
}
