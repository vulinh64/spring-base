package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record ActionUser(UUID id, String username) implements RecordBaseActionUser {}
