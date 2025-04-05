package com.vulinh.exception;

import com.vulinh.data.dto.message.I18NCapable;
import java.io.Serial;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

  @Serial private static final long serialVersionUID = 6246488964781153239L;

  private final I18NCapable errorKey;
  private final transient Object[] args;

  public CommonException(
      String message, I18NCapable errorKey, Throwable throwable, Object... args) {
    super(message, throwable);
    this.errorKey = errorKey;
    this.args = args;
  }
}
