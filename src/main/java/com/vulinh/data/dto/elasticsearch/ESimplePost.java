package com.vulinh.data.dto.elasticsearch;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record ESimplePost(UUID id, String title, String shortContent)
    implements RecordUuidIdentifiable {}
