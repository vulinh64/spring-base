package com.vulinh.data.elasticsearch;

import com.vulinh.data.document.EPost;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EPostRepository extends ElasticsearchRepository<EPost, UUID> {

  Page<EPost> findByTitleContainingIgnoreCaseOrPostContentContainingIgnoreCase(
      String title, String content, Pageable pageable);
}
