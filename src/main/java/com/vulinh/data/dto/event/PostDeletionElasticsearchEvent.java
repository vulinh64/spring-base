package com.vulinh.data.dto.event;

import com.vulinh.data.document.EPost;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record PostDeletionElasticsearchEvent(EPost elasticsearchDocument)
    implements WithDocumentElasticsearchEvent {}
