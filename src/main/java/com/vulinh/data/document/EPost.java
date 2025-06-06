package com.vulinh.data.document;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.elasticsearch.annotations.Document;

@With
@Builder
@Document(indexName = "post")
public record EPost(UUID id, String title, String excerpt, String slug, String postContent)
    implements RecordUuidIdentifiable, ElasticsearchDocument {}
