package com.vulinh.configuration;

import com.vulinh.data.dto.security.CustomAuthentication;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.CustomAuthenticationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Primary
public class CustomAuthenticationManager implements AuthenticationManager {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public CustomAuthentication authenticate(Authentication authentication) {
    if (!(authentication.getPrincipal() instanceof JwtPayload payload)) {
      throw new InternalAuthenticationServiceException(
          "Invalid authentication principal: %s".formatted(authentication));
    }

    return userRepository
        .findByIdAndIsActiveIsTrue(payload.subject())
        .map(UserMapper.INSTANCE::toBasicUserDTO)
        .map(CustomAuthenticationFactory.INSTANCE::fromUserBasicDTO)
        .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);
  }
}
