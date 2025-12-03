package com.vulinh.data.event;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record ActionUser(UUID id, String username) implements RecordUuidIdentifiable {}
