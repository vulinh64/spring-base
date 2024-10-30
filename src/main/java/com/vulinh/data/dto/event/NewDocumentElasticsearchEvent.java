package com.vulinh.data.dto.event;

import com.vulinh.data.document.ElasticsearchDocument;

public sealed interface NewDocumentElasticsearchEvent permits NewPostElasticsearchEvent {

  ElasticsearchDocument elasticsearchDocument();
}
