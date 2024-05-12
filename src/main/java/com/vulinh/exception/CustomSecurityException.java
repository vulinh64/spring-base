package com.vulinh.exception;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.message.I18NCapable;
import java.io.Serial;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
public class CustomSecurityException extends RuntimeException implements I18NCapable {

  @Serial private static final long serialVersionUID = -5695406504280010106L;

  private final CommonMessage errorCode;
  private final transient Object[] args;

  public CustomSecurityException(String message, CommonMessage errorCode) {
    this(message, errorCode, null);
  }

  public CustomSecurityException(
      String message, CommonMessage errorCode, @Nullable Throwable throwable, Object... args) {
    super(message, throwable);
    this.errorCode = errorCode;
    this.args = args;
  }

  @Override
  @NonNull
  public String getCode() {
    return errorCode.getCode();
  }
}
