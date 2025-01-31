package com.vulinh.locale;

import com.vulinh.data.dto.message.WithHttpStatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public enum CommonMessage implements WithHttpStatusCode {
  MESSAGE_SUCCESS("000", HttpStatus.OK),
  MESSAGE_INTERNAL_ERROR("M9999", HttpStatus.INTERNAL_SERVER_ERROR),
  MESSAGE_INVALID_USERNAME("M0001", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_PASSWORD("M0002", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_EMAIL("M0003", HttpStatus.BAD_REQUEST),
  MESSAGE_USER_EXISTED("M0004", HttpStatus.CONFLICT),
  MESSAGE_EMAIL_EXISTED("M0005", HttpStatus.CONFLICT),
  MESSAGE_INVALID_NEW_PASSWORD("M0006", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_CONFIRMATION("M0007", HttpStatus.NOT_FOUND),
  MESSAGE_PASSWORD_MISMATCHED("M0008", HttpStatus.BAD_REQUEST),
  MESSAGE_SAME_OLD_PASSWORD("M0009", HttpStatus.CONFLICT),
  MESSAGE_NO_SELF_DESTRUCTION("M0010", HttpStatus.FORBIDDEN),
  MESSAGE_INVALID_ENTITY_ID("M0011", HttpStatus.NOT_FOUND),

  MESSAGE_POST_INVALID_TITLE("M1000", HttpStatus.BAD_REQUEST),
  MESSAGE_POST_INVALID_CONTENT("M1001", HttpStatus.BAD_REQUEST),
  MESSAGE_POST_INVALID_SLUG("M1002", HttpStatus.BAD_REQUEST),
  MESSAGE_POST_INVALID_TAG("M1004", HttpStatus.BAD_REQUEST),

  MESSAGE_COMMENT_INVALID("M1005", HttpStatus.BAD_REQUEST),
  MESSAGE_COMMENT_TOO_LONG("M1006", HttpStatus.BAD_REQUEST),

  MESSAGE_INVALID_CATEGORY("M2000", HttpStatus.BAD_REQUEST),
  MESSAGE_EXISTED_CATEGORY("M2001", HttpStatus.CONFLICT),
  MESSAGE_INVALID_CATEGORY_SLUG("M2002", HttpStatus.BAD_REQUEST),
  MESSAGE_DEFAULT_CATEGORY_IMMORTAL("M2003", HttpStatus.FORBIDDEN),

  MESSAGE_INVALID_CREDENTIALS("M0401", HttpStatus.UNAUTHORIZED),
  MESSAGE_INVALID_AUTHORIZATION("M0403", HttpStatus.FORBIDDEN),
  MESSAGE_CREDENTIALS_EXPIRED("M0404", HttpStatus.UNAUTHORIZED),
  MESSAGE_INVALID_PUBLIC_KEY_CONFIG("M0405", HttpStatus.INTERNAL_SERVER_ERROR),
  MESSAGE_INVALID_PRIVATE_KEY_CONFIG("M0406", HttpStatus.INTERNAL_SERVER_ERROR),
  MESSAGE_INVALID_BODY_REQUEST("M0407", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_OWNER_OR_NO_RIGHT("M0408", HttpStatus.FORBIDDEN),
  MESSAGE_INVALID_TOKEN_TYPE("M0409", HttpStatus.FORBIDDEN),
  MESSAGE_INVALID_SESSION("M0410", HttpStatus.FORBIDDEN),
  MESSAGE_INVALID_TOKEN("M0411", HttpStatus.BAD_REQUEST);

  private final String errorCode;
  private final HttpStatusCode httpStatusCode;
}
