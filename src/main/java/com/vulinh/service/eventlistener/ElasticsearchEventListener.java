package com.vulinh.service.eventlistener;

import com.vulinh.data.dto.event.PostDeletionElasticsearchEvent;
import com.vulinh.data.dto.event.PostElasticsearchEvent;
import com.vulinh.data.dto.event.WithDocumentElasticsearchEvent;
import com.vulinh.data.elasticsearch.EPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchEventListener {

  private final EPostRepository ePostRepository;

  @EventListener
  public void listenToElasticsearchEvent(WithDocumentElasticsearchEvent event) {
    try {
      switch (event) {
        case PostElasticsearchEvent(var post) -> ePostRepository.save(post);
        case PostDeletionElasticsearchEvent(var post) -> ePostRepository.delete(post);
      }
    } catch (Exception e) {
      log.warn("Error while handling Elasticsearch event", e);
    }
  }
}
