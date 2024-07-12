package com.vulinh.data.dto.post;

import java.io.Serializable;
import java.util.UUID;

public record AuthorDTO(UUID id, String fullName, String username) implements Serializable {}
