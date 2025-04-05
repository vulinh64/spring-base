package com.vulinh.data.dto.category;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record CategoryDTO(UUID id, String categorySlug, String displayName)
    implements RecordUuidIdentifiable {}
