package com.vulinh.utils.security;

import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.entity.Users;
import java.util.UUID;

public interface AccessTokenGenerator {

  AccessTokenContainer generateAccessToken(Users users, UUID sessionId);
}
