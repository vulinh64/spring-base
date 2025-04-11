package com.vulinh.data.dto.response;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record CategoryResponse(UUID id, String categorySlug, String displayName)
    implements RecordUuidIdentifiable {}
