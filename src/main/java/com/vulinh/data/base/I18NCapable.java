package com.vulinh.data.base;

import com.vulinh.locale.LocalizationSupport;
import java.io.Serializable;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

public interface I18NCapable extends Serializable {

  @NonNull
  String getErrorCode();

  default String getDisplayMessage(Object... args) {
    return LocalizationSupport.getParsedMessage(getErrorCode(), args);
  }

  @NonNull
  HttpStatusCode getHttpStatusCode();
}
