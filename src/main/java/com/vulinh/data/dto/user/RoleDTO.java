package com.vulinh.data.dto.user;

import com.vulinh.constant.UserRole;
import java.io.Serializable;

import com.vulinh.data.base.RecordIdentifiable;
import lombok.*;

@Builder
@With
public record RoleDTO(UserRole id, int superiority)
    implements RecordIdentifiable<UserRole>, Serializable {}
