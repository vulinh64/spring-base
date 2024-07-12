package com.vulinh.data.dto.category;

import java.io.Serializable;
import java.util.UUID;

public record CategoryDTO(UUID id, String categorySlug, String displayName)
    implements Serializable {}
