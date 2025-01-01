package com.vulinh.data.repository;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.factory.ExceptionFactory;
import java.util.UUID;

import com.vulinh.locale.CommonMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends BaseRepository<UserSession, UserSessionId> {

  default UserSession findUserSession(UserSessionId userSessionId) {
    return findById(userSessionId)
        .orElseThrow(
            () ->
                ExceptionFactory.INSTANCE.buildCommonException(
                    "Session ID %s for user ID %s did not exist or has been invalidated"
                        .formatted(userSessionId.sessionId(), userSessionId.userId()),
                    CommonMessage.MESSAGE_INVALID_SESSION));
  }

  default UserSession findUserSession(UUID userId, UUID sessionId) {
    return findUserSession(UserSessionId.of(userId, sessionId));
  }
}
