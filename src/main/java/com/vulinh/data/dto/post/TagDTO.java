package com.vulinh.data.dto.post;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record TagDTO(UUID id, String displayName) implements RecordUuidIdentifiable, Serializable {}
