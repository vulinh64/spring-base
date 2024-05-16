package com.vulinh.utils.security;

import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.entity.Users;

public interface AccessTokenGenerator {

  AccessToken generateAccessToken(Users users);
}
