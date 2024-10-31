package com.vulinh.data.dto.event;

import com.vulinh.data.document.EPost;
import lombok.With;

@With
public record PostDeletionElasticsearchEvent(EPost elasticsearchDocument)
    implements WithDocumentElasticsearchEvent {
}
