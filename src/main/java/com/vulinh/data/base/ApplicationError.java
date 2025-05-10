package com.vulinh.data.base;

import com.vulinh.locale.LocalizationSupport;
import org.springframework.lang.NonNull;

public interface ApplicationError {

  @NonNull
  String getErrorCode();

  @NonNull
  default String getDisplayMessage(Object... args) {
    return LocalizationSupport.getParsedMessage(this, args);
  }
}
