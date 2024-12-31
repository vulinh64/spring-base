package com.vulinh.service.sessions;

import com.vulinh.data.dto.security.AccessTokenContainer;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.data.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSessionService {

  private final UserSessionRepository userSessionRepository;

  @Transactional
  public TokenResponse createUserSession(AccessTokenContainer container) {
    userSessionRepository.save(
        UserSession.builder()
            .id(UserSessionId.of(container.userId(), container.sessionId()))
            .build());

    return container.tokenResponse();
  }
}
