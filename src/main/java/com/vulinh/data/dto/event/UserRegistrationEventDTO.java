package com.vulinh.data.dto.event;

import com.vulinh.data.entity.Users;
import org.springframework.lang.NonNull;

public record UserRegistrationEventDTO(@NonNull Users user) {

  public static UserRegistrationEventDTO of(Users users) {
    return new UserRegistrationEventDTO(users);
  }
}
