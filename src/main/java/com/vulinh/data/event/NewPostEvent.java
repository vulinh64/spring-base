package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record NewPostEvent(UUID postId, String title, String excerpt) {}
