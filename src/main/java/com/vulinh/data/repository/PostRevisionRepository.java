package com.vulinh.data.repository;

import com.vulinh.constant.NamedQueryConstant;
import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.dto.post.PostRevisionDTO;
import com.vulinh.data.entity.PostRevision;
import com.vulinh.data.entity.PostRevisionId;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PostRevisionRepository extends BaseRepository<PostRevision, PostRevisionId> {

  @Query(name = NamedQueryConstant.FIND_POST_REVISIONS)
  Page<PostRevisionDTO> findByPostIdOrPostSlug(UUID postId, Pageable pageable);
}
