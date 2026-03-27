package com.vulinh.exception;

import module java.base;

import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.ApplicationError;
import lombok.Getter;

/// Exception thrown when user-submitted content contains disallowed HTML tags or attributes
/// that fail Jsoup's safety validation. This exception carries detailed context for backend
/// investigation: the field name where the violation was detected, the offending content
/// (truncated), and the sanitized version showing what Jsoup would have produced.
@Getter
public class XSSViolationException extends ApplicationException {

  @Serial private static final long serialVersionUID = 0L;

  private static final int CONTENT_PREVIEW_LENGTH = 500;

  final String fieldName;
  final String offendingContent;
  final String sanitizedContent;

  public static XSSViolationException of(
      String fieldName, String rawContent, String sanitizedContent) {
    var truncatedRaw = truncate(rawContent);
    var truncatedSanitized = truncate(sanitizedContent);

    return new XSSViolationException(
        "XSS violation in field [%s]: content contains disallowed HTML. Raw (truncated): [%s], Sanitized: [%s]"
            .formatted(fieldName, truncatedRaw, truncatedSanitized),
        ServiceErrorCode.MESSAGE_XSS_VIOLATION,
        fieldName,
        truncatedRaw,
        truncatedSanitized);
  }

  XSSViolationException(
      String message,
      ApplicationError applicationError,
      String fieldName,
      String offendingContent,
      String sanitizedContent) {
    super(message, applicationError, null, fieldName);
    this.fieldName = fieldName;
    this.offendingContent = offendingContent;
    this.sanitizedContent = sanitizedContent;
  }

  private static String truncate(String text) {
    if (text == null) {
      return "";
    }
    return text.length() > CONTENT_PREVIEW_LENGTH
        ? text.substring(0, CONTENT_PREVIEW_LENGTH) + "..."
        : text;
  }
}
