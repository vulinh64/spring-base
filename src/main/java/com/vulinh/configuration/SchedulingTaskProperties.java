package com.vulinh.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schedule")
public record SchedulingTaskProperties(String cleanExpiredUserSessions) {}
