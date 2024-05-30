package com.vulinh.factory;

import com.vulinh.data.dto.event.UserRegistrationEventDTO;
import com.vulinh.data.entity.Users;

@SuppressWarnings("java:S6548")
public enum UserRegistrationEventFactory {
  INSTANCE;

  public UserRegistrationEventDTO fromUser(Users users) {
    return new UserRegistrationEventDTO(users);
  }
}
