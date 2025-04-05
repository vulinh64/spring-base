package com.vulinh.data.dto.user;

import com.vulinh.constant.UserRole;
import com.vulinh.data.base.RecordIdentifiable;
import java.io.Serializable;
import lombok.*;

@With
@Builder
public record RoleDTO(UserRole id, int superiority)
    implements RecordIdentifiable<UserRole>, Serializable {}
