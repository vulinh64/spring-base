package com.vulinh.locale;

import module java.base;

public final class ServiceLocalizationBundleProvider implements LocalizationBundleProvider {

  @Override
  public Collection<String> bundleNames() {
    return List.of("i18n/messages");
  }
}
