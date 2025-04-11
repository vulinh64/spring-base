package com.vulinh.configuration;

import com.vulinh.configuration.data.CustomAuthentication;
import com.vulinh.data.dto.carrier.DecodedJwtPayloadCarrier;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.service.sessions.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

  private final UserRepository userRepository;

  private final UserSessionService userSessionService;

  @Override
  @Transactional
  @NonNull
  public CustomAuthentication authenticate(Authentication authentication) {
    if (!(authentication.getPrincipal()
        instanceof DecodedJwtPayloadCarrier(var userId, var sessionId))) {
      throw new InternalAuthenticationServiceException(
          "Invalid authentication principal: %s".formatted(authentication));
    }

    var userDTO =
        UserMapper.INSTANCE.toBasicUserDTO(
            userRepository.findActiveUser(userId),
            userSessionService.findUserSession(userId, sessionId));

    return new CustomAuthentication(userDTO);
  }
}
