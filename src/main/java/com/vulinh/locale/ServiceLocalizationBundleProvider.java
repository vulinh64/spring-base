package com.vulinh.locale;

import java.util.Collection;
import java.util.List;

public final class ServiceLocalizationBundleProvider implements LocalizationBundleProvider {

  @Override
  public Collection<String> bundleNames() {
    return List.of("i18n/messages");
  }
}
