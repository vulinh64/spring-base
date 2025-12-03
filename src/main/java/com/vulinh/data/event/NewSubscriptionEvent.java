package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record NewSubscriptionEvent(
    ActionUser actionUser, UUID subscribedUserId, String subscribedUsername)
    implements RecordBaseEvent {}
