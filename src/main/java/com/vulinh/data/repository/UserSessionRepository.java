package com.vulinh.data.repository;

import com.vulinh.configuration.cache.FromUserSessionEntityKeyGenerator;
import com.vulinh.constant.CacheConstant;
import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends BaseRepository<UserSession, UserSessionId> {

  @Override
  @NonNull
  @CachePut(
      cacheNames = CacheConstant.USER_SESSION_CACHE,
      keyGenerator = FromUserSessionEntityKeyGenerator.BEAN_NAME)
  <S extends UserSession> S save(@NonNull S entity);

  @Override
  @NonNull
  @Cacheable(cacheNames = CacheConstant.USER_SESSION_CACHE)
  Optional<UserSession> findById(@NonNull UserSessionId userSessionId);

  @Override
  @CacheEvict(cacheNames = CacheConstant.USER_SESSION_CACHE)
  void deleteById(@NonNull UserSessionId userSessionId);
}
