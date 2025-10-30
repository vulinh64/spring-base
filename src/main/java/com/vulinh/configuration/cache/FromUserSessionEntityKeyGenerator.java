package com.vulinh.configuration.cache;

import module java.base;

import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component(FromUserSessionEntityKeyGenerator.BEAN_NAME)
@Slf4j
public class FromUserSessionEntityKeyGenerator implements KeyGenerator {

  public static final String BEAN_NAME = "fromUserSessionEntity";

  @Override
  @NonNull
  public UserSessionId generate(@NonNull Object target, @NonNull Method method, Object... params) {
    var obj = params[0];

    if (!(obj instanceof UserSession session)) {
      throw new ClassCastException(
          "Expected class %s, got %s instead".formatted(UserSession.class, obj.getClass()));
    }

    var userSessionId = session.getId();

    log.debug("{}#{} | key = {}", method.getDeclaringClass(), method.getName(), userSessionId);

    return userSessionId;
  }
}
