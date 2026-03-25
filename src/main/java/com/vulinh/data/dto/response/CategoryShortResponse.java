package com.vulinh.data.dto.response;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record CategoryShortResponse(String categorySlug, String displayName) {}
