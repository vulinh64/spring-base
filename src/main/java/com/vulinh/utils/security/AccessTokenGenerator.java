package com.vulinh.utils.security;

import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.entity.Users;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface AccessTokenGenerator {

  @NonNull
  AccessTokenContainer generateAccessToken(Users users, UUID sessionId);
}
