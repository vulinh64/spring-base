package com.vulinh.schedule;

import module java.base;

import com.vulinh.data.constant.CacheConstant;
import com.vulinh.data.entity.QUserSession;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.repository.UserSessionRepository;
import com.vulinh.utils.PredicateBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/// This class is just a simple example to demonstrate Spring Boot's scheduling capabilities; in a microservices
/// architecture, scheduling tasks should be handled by a dedicated service separate from the main business logic
/// service.
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanExpiredUserSessionService {

  static final int ITEMS_PER_BATCH = 10;

  final UserSessionRepository userSessionRepository;

  final CacheManager cacheManager;

  final SchedulingTaskSupportService schedulingTaskSupportService;

  @PostConstruct
  public void info() {
    var cronExpression = schedulingTaskSupportService.cleanExpiredUserSessionsExpression();

    log.info(
        "Expired user session cleaning bean enabled, cron expression: {}, next recent execution will be at {}",
        cronExpression,
        CronExpression.parse(cronExpression).next(LocalDateTime.now()));
  }

  @Scheduled(cron = "#{schedulingTaskSupportService.cleanExpiredUserSessionsExpression()}")
  @Transactional
  public void cleanExpiredUserSessions() {
    log.info("Started cleaning expired user sessions...");

    var qUserSession = QUserSession.userSession;

    var userSessions =
        userSessionRepository.findAll(
            qUserSession.expirationDate.lt(Instant.now()),
            PageRequest.of(
                0,
                ITEMS_PER_BATCH,
                Sort.by(Order.asc(PredicateBuilder.getFieldName(qUserSession.expirationDate)))));

    userSessionRepository.deleteAllInBatch(userSessions);

    Optional.ofNullable(cacheManager.getCache(CacheConstant.USER_SESSION_CACHE))
        .ifPresent(
            userSessionCache ->
                userSessions.stream()
                    .map(UserSession::getId)
                    .forEach(userSessionCache::evictIfPresent));
  }
}
