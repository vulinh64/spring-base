package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record NewCommentEvent(UUID postId, String title, ActionUser actionUser, String comment)
    implements RecordBaseEvent {}
