package com.vulinh.data.dto.event;

import com.vulinh.data.document.EPost;

public record NewPostElasticsearchEvent(EPost elasticsearchDocument)
    implements NewDocumentElasticsearchEvent {

  public static NewPostElasticsearchEvent of(EPost elasticsearchDocument) {
    return new NewPostElasticsearchEvent(elasticsearchDocument);
  }
}
