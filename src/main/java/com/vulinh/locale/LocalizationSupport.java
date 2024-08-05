package com.vulinh.locale;

import java.util.*;
import java.util.ResourceBundle.Control;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalizationSupport {

  private static final String BASE_NAME = "i18n";

  private static final Control BUNDLE_CONTROL =
      new MultiResourceBundleControl(List.of("i18n/messages"));

  private static final Map<Locale, ResourceBundle> LOCALE_RESOURCE_MAP = new HashMap<>();

  @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
  private void clearMap() {
    if (!LOCALE_RESOURCE_MAP.isEmpty()) {
      log.info("Locale-Resource map {} cleared", LOCALE_RESOURCE_MAP.keySet());
      LOCALE_RESOURCE_MAP.clear();
    }
  }

  @NonNull
  public static String getParsedMessage(@NonNull String code, Object... args) {
    try {
      var locale = LocaleContextHolder.getLocale();

      var resourceBundle = getResourceBundleLazy(locale);

      return Optional.of(resourceBundle.getString(code))
          .filter(StringUtils::isNotBlank)
          .map(rawMessage -> ArrayUtils.isEmpty(args) ? rawMessage : rawMessage.formatted(args))
          .orElse(code);
    } catch (Exception exception) {
      log.warn("Locale error", exception);
      return code;
    }
  }

  private static ResourceBundle getResourceBundleLazy(Locale locale) {
    return LOCALE_RESOURCE_MAP.computeIfAbsent(locale, LocalizationSupport::compute);
  }

  private static ResourceBundle compute(@Nullable Locale locale) {
    var actualLocale = Optional.ofNullable(locale).orElseGet(LocaleContextHolder::getLocale);

    var resourceBundle = ResourceBundle.getBundle(BASE_NAME, actualLocale, BUNDLE_CONTROL);

    LOCALE_RESOURCE_MAP.put(actualLocale, resourceBundle);

    return resourceBundle;
  }
}
