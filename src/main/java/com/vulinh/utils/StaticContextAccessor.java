package com.vulinh.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("java:S3010")
public class StaticContextAccessor {

  public static <T> T getBean(Class<T> clazz) {
    return staticContext.getBean(clazz);
  }

  private static ApplicationContext staticContext;

  @Autowired
  private StaticContextAccessor(ApplicationContext applicationContext) {
    staticContext = applicationContext;
  }
}
