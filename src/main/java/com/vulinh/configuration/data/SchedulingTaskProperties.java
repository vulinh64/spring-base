package com.vulinh.configuration.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schedule")
public record SchedulingTaskProperties(String cleanExpiredUserSessions) {}
