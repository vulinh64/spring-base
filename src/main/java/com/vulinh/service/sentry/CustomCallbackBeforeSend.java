package com.vulinh.service.sentry;

import com.vulinh.exception.CommonException;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomCallbackBeforeSend implements SentryOptions.BeforeSendCallback {

  @Override
  public SentryEvent execute(@NonNull SentryEvent event, @NonNull Hint hint) {
    var throwable = event.getThrowable();

    if (StringUtils.isNotBlank(event.getTransaction()) && throwable instanceof CommonException) {
      log.info("Handled error thrown from a transactional | {}", throwable.getMessage());

      return null;
    }

    return event;
  }
}
