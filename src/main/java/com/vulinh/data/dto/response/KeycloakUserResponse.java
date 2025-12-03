package com.vulinh.data.dto.response;

import module java.base;

import com.vulinh.data.base.RecordUuidIdentifiable;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record KeycloakUserResponse(
    UUID id, String username, String firstName, String lastName, String email, boolean isEnabled)
    implements RecordUuidIdentifiable {}
