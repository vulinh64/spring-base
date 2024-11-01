package com.vulinh.configuration;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MyP6SpyLogging extends Slf4JLogger {

  @Override
  public void logSQL(
      int connectionId,
      String time,
      long elapsed,
      Category category,
      String prepared,
      String sql,
      String url) {
    switch (category.getName().toUpperCase()) {
      case "ERROR" -> log.error("(ERROR) {}", sql);
      case "WARN" -> log.warn("(WARN)  {}", sql);
      case "DEBUG" -> log.debug("(DEBUG) {}", sql);
      case "TRACE" -> log.trace("(TRACE) {}", sql);
      case "COMMIT" -> log.info("Connection #{} committed...", connectionId);
      case "ROLLBACK" -> log.info("Connection #{} rolled back...", connectionId);
      case "OUTAGE" -> log.warn("Connection #{} encountered outage...", connectionId);
      default -> {
        if (StringUtils.isNotBlank(sql)) {
          log.info("\n\n{}\n",sql);
        }
      }
    }
  }
}
