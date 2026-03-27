package com.vulinh.data.projection;

import module java.base;

public record CategoryPostCountProjection(UUID id, String categorySlug, String displayName, long postCount) {}
