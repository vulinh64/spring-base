package com.vulinh.data.dto.category;

import java.io.Serializable;

public record CategoryDTO(String id, String categorySlug, String displayName)
    implements Serializable {}
