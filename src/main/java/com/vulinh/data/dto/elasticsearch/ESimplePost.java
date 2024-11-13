package com.vulinh.data.dto.elasticsearch;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.util.UUID;

public record ESimplePost(UUID id, String title, String shortContent)
    implements RecordUuidIdentifiable {}
