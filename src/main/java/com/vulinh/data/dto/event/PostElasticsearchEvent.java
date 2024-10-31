package com.vulinh.data.dto.event;

import com.vulinh.data.document.EPost;

public record PostElasticsearchEvent(EPost elasticsearchDocument)
    implements WithDocumentElasticsearchEvent {

  public static PostElasticsearchEvent of(EPost elasticsearchDocument) {
    return new PostElasticsearchEvent(elasticsearchDocument);
  }
}
