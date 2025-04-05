package com.vulinh.data.dto.category;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record CategorySearchDTO(String displayName, String categorySlug) {}
