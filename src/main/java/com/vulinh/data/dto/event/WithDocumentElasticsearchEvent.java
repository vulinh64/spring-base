package com.vulinh.data.dto.event;

import com.vulinh.data.document.ElasticsearchDocument;

public sealed interface WithDocumentElasticsearchEvent permits PostElasticsearchEvent {

  ElasticsearchDocument elasticsearchDocument();
}
