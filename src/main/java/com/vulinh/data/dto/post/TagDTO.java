package com.vulinh.data.dto.post;

import java.io.Serializable;
import java.util.UUID;

public record TagDTO(UUID id, String displayName) implements Serializable {}
