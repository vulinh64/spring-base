package com.vulinh.service.sessions;

import module java.base;

import com.vulinh.data.dto.carrier.AccessTokenCarrier;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.data.repository.UserSessionRepository;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.locale.ServiceErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSessionService {

  private final UserSessionRepository userSessionRepository;

  @Transactional
  public TokenResponse createUserSession(AccessTokenCarrier container) {
    userSessionRepository.save(
        UserSession.builder()
            .id(UserSessionId.of(container.userId(), container.sessionId()))
            .expirationDate(container.refreshTokenExpirationDate())
            .build());

    return container.tokenResponse();
  }

  @Transactional
  public void updateUserSession(UserSession userSession, Instant expirationDate) {
    userSessionRepository.save(userSession.setExpirationDate(expirationDate));
  }

  public UserSession findUserSession(UUID userId, UUID sessionId) {
    var userSessionId = UserSessionId.of(userId, sessionId);

    return userSessionRepository
        .findById(userSessionId)
        .orElseThrow(
            () ->
                AuthorizationException.invalidAuthorization(
                    "Session ID %s for user ID %s did not exist or has been invalidated"
                        .formatted(userSessionId.sessionId(), userSessionId.userId()),
                    ServiceErrorCode.MESSAGE_INVALID_SESSION));
  }

  public void deleteUserSession(UserSession userSession) {
    userSessionRepository.delete(userSession);
  }
}
