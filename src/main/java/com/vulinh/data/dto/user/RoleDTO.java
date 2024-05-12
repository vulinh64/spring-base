package com.vulinh.data.dto.user;

import java.io.Serializable;
import lombok.*;

@Builder
@With
public record RoleDTO(String id, int superiority) implements Serializable {}
