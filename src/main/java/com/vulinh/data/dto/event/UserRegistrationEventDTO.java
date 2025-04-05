package com.vulinh.data.dto.event;

import com.vulinh.data.entity.Users;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record UserRegistrationEventDTO(Users user) {}
