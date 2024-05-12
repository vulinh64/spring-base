package com.vulinh.data.dto.post;

import java.io.Serializable;

public record AuthorDTO(String id, String fullName, String username) implements Serializable {}
