package com.vulinh.configuration.cache;

import com.vulinh.data.entity.ids.UserSessionId;
import java.lang.reflect.Method;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component(FetchUserSessionKeyGenerator.BEAN_NAME)
@Slf4j
public class FetchUserSessionKeyGenerator implements KeyGenerator {

  public static final String BEAN_NAME = "fetchUserSessionKeyGenerator";

  @Override
  @NonNull
  public Object generate(
      @NonNull Object target, @NonNull Method method, @NonNull Object... params) {
    var obj1 = params[0];
    var obj2 = params[1];

    if (!(obj1 instanceof UUID userId) || !(obj2 instanceof UUID sessionId)) {
      throw new ClassCastException(
          "Expected %s for both objects, but got %s and %s instead"
              .formatted(UUID.class, obj1.getClass(), obj2.getClass()));
    }

    var userSessionId = UserSessionId.of(userId, sessionId);

    return Common.logAndReturn(
        userSessionId, "User session fetching operation, using key {}", userSessionId);
  }
}
