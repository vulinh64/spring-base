package com.vulinh.locale;

import module java.base;

import com.vulinh.data.base.ApplicationError;
import java.util.ResourceBundle.Control;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalizationSupport {

  private static final String BASE_NAME = "i18n";

  private static final Control BUNDLE_CONTROL =
      new MultiResourceBundleControl(List.of("i18n/messages"));

  @NonNull
  public static String getParsedMessage(ApplicationError applicationError, Object... args) {
    return getParsedMessage(applicationError.getErrorCode(), args);
  }

  @NonNull
  public static String getParsedMessage(@NonNull String code, Object... args) {
    try {
      var locale = LocaleContextHolder.getLocale();

      var resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale, BUNDLE_CONTROL);

      return Optional.of(resourceBundle.getString(code))
          .filter(StringUtils::isNotBlank)
          .map(rawMessage -> ArrayUtils.isEmpty(args) ? rawMessage : rawMessage.formatted(args))
          .orElse(code);
    } catch (Exception exception) {
      log.warn("Locale error: {}", exception.getMessage());
      return code;
    }
  }
}
