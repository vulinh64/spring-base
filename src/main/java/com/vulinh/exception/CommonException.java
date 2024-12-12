package com.vulinh.exception;

import com.vulinh.data.dto.message.WithHttpStatusCode;
import java.io.Serial;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

  @Serial private static final long serialVersionUID = 944218762645229018L;

  private final WithHttpStatusCode errorKey;
  private final transient Object[] args;

  public CommonException(
      String message, WithHttpStatusCode errorKey, Throwable throwable, Object... args) {
    super(message, throwable);
    this.errorKey = errorKey;
    this.args = args;
  }
}
