package com.vulinh.data.event;

import module java.base;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record NewSubscriptionEvent(UUID subscribedUserId, String subscribedUsername) {}
