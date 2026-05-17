package com.vulinh.configuration;

import com.vulinh.configuration.CaffeineCacheConfiguration.CacheProperties;
import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.event.EventMessageWrapper;
import com.vulinh.data.event.payload.KeyInvalidatedEvent;
import com.vulinh.security.AbstractKeyInvalidationListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtKeyInvalidationConfig extends AbstractKeyInvalidationListener {

  private final CacheManager cacheManager;
  private final ApplicationProperties applicationProperties;

  @Override
  protected void onKeyInvalidated(EventMessageWrapper<KeyInvalidatedEvent> event) {
    var eventId = event.eventId();

    // RabbitMQ redelivery on ack failure can re-invoke this with the same eventId.
    // putIfAbsent is the atomic test-and-set: returns non-null iff we've seen this id.
    var dedup = cacheManager.getCache(CacheProperties.KEY_INVALIDATION_DEDUP_CACHE);

    if (dedup != null && dedup.putIfAbsent(eventId, Boolean.TRUE) != null) {
      log.debug("Duplicate KEY_INVALIDATED ignored (eventId={})", eventId);
      return;
    }

    var payload = event.data();
    var jwkSetUri = applicationProperties.security().jwkSetUri();

    var jwksCache = cacheManager.getCache(CacheProperties.JWKS_CACHE);

    if (jwksCache != null) {
      jwksCache.evict(jwkSetUri);
    }

    log.info(
        "JWKS cache evicted on key-invalidation event (kid={}, reason={}, issuer={}, eventId={})",
        payload.kid(),
        payload.reason(),
        payload.issuer(),
        eventId);
  }
}
