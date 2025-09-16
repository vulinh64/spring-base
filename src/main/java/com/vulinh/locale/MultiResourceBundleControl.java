package com.vulinh.locale;

import module java.base;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
class MultiResourceBundleControl extends ResourceBundle.Control {

  @NonNull private final Collection<String> bundleNames;

  @Override
  public ResourceBundle newBundle(
      String baseName, Locale locale, String format, ClassLoader loader, boolean reload) {
    return new MultiResourceBundle(
        bundleNames.stream()
            .filter(StringUtils::isNotBlank)
            .map(bundleName -> ResourceBundle.getBundle(bundleName, locale))
            .toList());
  }

  @RequiredArgsConstructor
  static class MultiResourceBundle extends ResourceBundle {

    @NonNull private final Collection<ResourceBundle> resourceBundles;

    @Override
    public Object handleGetObject(@NonNull String key) {
      return resourceBundles.stream()
          .filter(Objects::nonNull)
          .filter(bundle -> bundle.containsKey(key))
          .map(e -> e.getObject(key))
          .findFirst()
          .orElse(null);
    }

    @Override
    @NonNull
    public Enumeration<String> getKeys() {
      return Collections.enumeration(
          resourceBundles.stream()
              .filter(Objects::nonNull)
              .map(ResourceBundle::getKeys)
              .map(Collections::list)
              .flatMap(Collection::stream)
              .toList());
    }
  }
}
