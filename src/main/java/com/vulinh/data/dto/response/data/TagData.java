package com.vulinh.data.dto.response.data;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record TagData(UUID id, String displayName)
    implements RecordUuidIdentifiable, Serializable {}
