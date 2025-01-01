package com.vulinh.schedule;

import com.vulinh.constant.EnvironmentConstant;
import com.vulinh.data.entity.QUserSession;
import com.vulinh.data.repository.UserSessionRepository;
import com.vulinh.utils.PredicateBuilder;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * This class is just a simple example to demonstrate Spring Boot's scheduling capabilities; in a microservices
 * architecture, scheduling tasks should be handled by a dedicated service separate from the main business logic
 * service.
 */
@Profile(EnvironmentConstant.ENV_PRODUCTION)
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanExpiredUserSession {

  private static final int ITEMS_PER_BATCH = 10;

  private final UserSessionRepository userSessionRepository;

  @PostConstruct
  public void info() {
    log.info("Expired user session cleaning bean {} enabled", getClass().getCanonicalName());
  }

  @Scheduled(cron = "#{schedulingTaskSupport.cleanExpiredUserSessionsExpression()}")
  public void cleanExpiredUserSessions() {
    var qUserSession = QUserSession.userSession;

    var userSessions =
        userSessionRepository.findAll(
            qUserSession.expirationDate.lt(LocalDateTime.now()),
            PageRequest.of(
                0,
                ITEMS_PER_BATCH,
                Sort.by(Order.asc(PredicateBuilder.getFieldName(qUserSession.expirationDate)))));

    userSessionRepository.deleteAllInBatch(userSessions);
  }
}
