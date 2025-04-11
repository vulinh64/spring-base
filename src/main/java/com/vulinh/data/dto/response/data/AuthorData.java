package com.vulinh.data.dto.response.data;

import com.vulinh.data.base.RecordUuidIdentifiable;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record AuthorData(UUID id, String fullName, String username)
    implements RecordUuidIdentifiable, Serializable {}
