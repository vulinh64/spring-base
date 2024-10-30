package com.vulinh.service.eventlistener;

import com.vulinh.data.dto.event.NewPostElasticsearchEvent;
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
  public void listenToElasticsearchEvent(NewPostElasticsearchEvent event) {
    try {
      var document = event.elasticsearchDocument();

      ePostRepository.save(document);
    } catch (Exception e) {
      log.warn("Error while saving document to Elasticsearch", e);
    }
  }
}
