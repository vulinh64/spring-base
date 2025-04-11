package com.vulinh.data.dto.response.data;

import com.vulinh.data.base.RecordIdentifiable;
import com.vulinh.data.constant.UserRole;
import java.io.Serializable;
import lombok.*;

@With
@Builder
public record RoleData(UserRole id, int superiority)
    implements RecordIdentifiable<UserRole>, Serializable {}
