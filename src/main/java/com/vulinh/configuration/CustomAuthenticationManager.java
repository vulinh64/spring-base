package com.vulinh.configuration;

import com.vulinh.data.dto.security.CustomAuthentication;
import com.vulinh.data.dto.security.DecodedJwtPayload;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.data.repository.UserSessionRepository;
import com.vulinh.factory.CustomAuthenticationFactory;
import com.vulinh.factory.ExceptionFactory;
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

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private final UserRepository userRepository;
  private final UserSessionRepository userSessionRepository;

  @Override
  @Transactional
  @NonNull
  public CustomAuthentication authenticate(Authentication authentication) {
    if (!(authentication.getPrincipal() instanceof DecodedJwtPayload(var userId, var sessionId))) {
      throw new InternalAuthenticationServiceException(
          "Invalid authentication principal: %s".formatted(authentication));
    }

    var userSessionId = UserSessionId.of(userId, sessionId);

    var session =
        userSessionRepository
            .findById(userSessionId)
            .orElseThrow(() -> EXCEPTION_FACTORY.invalidUserSession(userSessionId));

    var user =
        userRepository
            .findByIdAndIsActiveIsTrue(userId)
            .orElseThrow(EXCEPTION_FACTORY::invalidAuthorization);

    var userDTO = UserMapper.INSTANCE.toBasicUserDTO(user, session);

    return CustomAuthenticationFactory.INSTANCE.fromUserBasicDTO(userDTO);
  }
}
