package com.vulinh.configuration;

import module java.base;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("NullableProblems")
@Configuration
@Slf4j
public class CacheConfiguration {

  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofDays(1))
        .evictionListener(keyInvalidationListener("evicted"))
        .removalListener(keyInvalidationListener("removed"))
        .executor(Executors.newVirtualThreadPerTaskExecutor());
  }

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object> caffeineConfig) {
    var cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(caffeineConfig);
    return cacheManager;
  }

  static RemovalListener<Object, Object> keyInvalidationListener(String action) {
    return (key, value, cause) ->
        log.debug(
            "Key {} with value {} has been {}. Reason: {}",
            key,
            StringUtils.abbreviate(String.valueOf(value), 300),
            action,
            cause.name());
  }
}
