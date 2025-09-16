package com.vulinh.data.dto.response.data;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record AuthorData(UUID id, String fullName, String username)
    implements RecordUuidIdentifiable, Serializable {}
