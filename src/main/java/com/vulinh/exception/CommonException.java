package com.vulinh.exception;

import com.vulinh.data.dto.message.I18NCapable;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import java.io.Serial;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class CommonException extends RuntimeException implements I18NCapable {

  @Serial private static final long serialVersionUID = 944218762645229018L;

  private final transient WithHttpStatusCode errorKey;
  private final transient Object[] args;

  public CommonException(
      String message, WithHttpStatusCode errorKey, Throwable throwable, Object... args) {
    super(message, throwable);
    this.errorKey = errorKey;
    this.args = args;
  }

  public CommonException(String message, WithHttpStatusCode errorKey, Object... args) {
    this(message, errorKey, null, args);
  }

  @Override
  @NonNull
  public String getCode() {
    return errorKey.getCode();
  }
}
