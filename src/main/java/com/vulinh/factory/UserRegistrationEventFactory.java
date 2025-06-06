package com.vulinh.factory;

import com.vulinh.data.dto.event.UserRegistrationEvent;
import com.vulinh.data.entity.Users;

@SuppressWarnings("java:S6548")
public enum UserRegistrationEventFactory {
  INSTANCE;

  public UserRegistrationEvent fromUser(Users users) {
    return new UserRegistrationEvent(users);
  }
}
