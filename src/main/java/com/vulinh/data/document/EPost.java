package com.vulinh.data.document;

import java.util.UUID;
import lombok.With;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "post")
@With
public record EPost(UUID id, String title, String excerpt, String slug, String postContent)
    implements ElasticsearchDocument {

  public record ESimplePost(UUID id, String title, String shortContent) {}
}
