package com.vulinh.data.dto.event;

import com.vulinh.data.document.EPost;

public record PostElasticsearchEvent(EPost elasticsearchDocument)
    implements WithDocumentElasticsearchEvent {}
