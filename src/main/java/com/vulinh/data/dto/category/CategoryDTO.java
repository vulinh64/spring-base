package com.vulinh.data.dto.category;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;

public record CategoryDTO(UUID id, String categorySlug, String displayName)
    implements RecordUuidIdentifiable {}
