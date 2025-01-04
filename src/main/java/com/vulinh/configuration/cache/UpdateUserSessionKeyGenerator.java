package com.vulinh.configuration.cache;

import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component(UpdateUserSessionKeyGenerator.BEAN_NAME)
@Slf4j
public class UpdateUserSessionKeyGenerator implements KeyGenerator {

  public static final String BEAN_NAME = "updateUserSessionKeyGenerator";

  @Override
  @NonNull
  public UserSessionId generate(@NonNull Object target, @NonNull Method method, Object... params) {
    var obj = params[0];

    if (!(obj instanceof UserSession session)) {
      throw new ClassCastException(
          "Expected class %s, got %s instead".formatted(UserSession.class, obj.getClass()));
    }

    var userSessionId = session.getId();

    return Common.logAndReturn(
        userSessionId, "User session generating operation, using key {}", userSessionId);
  }
}
