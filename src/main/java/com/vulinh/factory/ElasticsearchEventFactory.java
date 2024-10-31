package com.vulinh.factory;

import com.vulinh.data.document.EPost;
import com.vulinh.data.dto.event.PostDeletionElasticsearchEvent;
import com.vulinh.data.dto.event.PostElasticsearchEvent;
import com.vulinh.data.dto.event.WithDocumentElasticsearchEvent;

@SuppressWarnings("java:S6548")
public enum ElasticsearchEventFactory {
  INSTANCE;

  public WithDocumentElasticsearchEvent ofDeletion(EPost post) {
    return new PostDeletionElasticsearchEvent(post);
  }

  public WithDocumentElasticsearchEvent ofPersistence(EPost elasticsearchDocument) {
    return new PostElasticsearchEvent(elasticsearchDocument);
  }
}
