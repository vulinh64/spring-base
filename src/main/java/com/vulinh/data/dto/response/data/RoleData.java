package com.vulinh.data.dto.response.data;

import module java.base;

import com.vulinh.data.base.RecordIdentifiable;
import com.vulinh.data.constant.UserRole;
import lombok.*;

@With
@Builder
public record RoleData(UserRole id, int superiority)
    implements RecordIdentifiable<UserRole>, Serializable {}
