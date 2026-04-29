package com.vulinh.configuration;

import module java.base;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CaffeineCacheConfiguration {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class CacheProperties {

    public static final String ALL_CATEGORIES_CACHE = "all-categories-cache";
    public static final String JWKS_CACHE = "jwks-cache";
    public static final String KEY_INVALIDATION_DEDUP_CACHE = "key-invalidation-dedup-cache";
  }

  @Bean
  public CacheManager cacheManager() {
    var cacheManager = new CaffeineCacheManager();

    cacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).maximumSize(1000));

    return cacheManager;
  }
}
