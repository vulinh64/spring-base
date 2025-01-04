package com.vulinh.data.repository;

import com.vulinh.configuration.cache.FetchUserSessionKeyGenerator;
import com.vulinh.configuration.cache.UpdateUserSessionKeyGenerator;
import com.vulinh.constant.CacheConstant;
import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.locale.CommonMessage;
import java.util.UUID;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
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

  @Cacheable(
      cacheNames = CacheConstant.USER_SESSION_CACHE,
      keyGenerator = FetchUserSessionKeyGenerator.BEAN_NAME)
  default UserSession findUserSession(UUID userId, UUID sessionId) {
    return findUserSession(UserSessionId.of(userId, sessionId));
  }

  @Override
  @NonNull
  @CachePut(
      cacheNames = CacheConstant.USER_SESSION_CACHE,
      keyGenerator = UpdateUserSessionKeyGenerator.BEAN_NAME)
  <S extends UserSession> S save(@NonNull S entity);
}
