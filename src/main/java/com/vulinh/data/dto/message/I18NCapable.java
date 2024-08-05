package com.vulinh.data.dto.message;

import com.vulinh.locale.LocalizationSupport;
import java.io.Serializable;
import org.springframework.lang.NonNull;

public interface I18NCapable extends Serializable {

  @NonNull
  String getCode();

  default String getMessage(Object... args) {
    return LocalizationSupport.getParsedMessage(getCode(), args);
  }
}
