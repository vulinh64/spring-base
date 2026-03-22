package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;

@Builder
public record AuthorResponse(UUID id, String username, String email)
    implements RecordUuidIdentifiable, Serializable {}
