package com.vulinh;

import module java.base;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MyP6SpyLogging extends Slf4JLogger {

  enum LogCategory {
    ERROR,
    WARN,
    INFO,
    DEBUG,
    BATCH,
    STATEMENT,
    RESULTSET,
    COMMIT,
    ROLLBACK,
    RESULT,
    OUTAGE;

    static final Set<String> SETS =
        Arrays.stream(LogCategory.values()).map(Enum::name).collect(Collectors.toSet());
  }

  @Override
  public void logSQL(
      int connectionId,
      String time,
      long elapsed,
      Category category,
      String prepared,
      String sql,
      String url) {
    var categoryName = category.getName().toUpperCase();

    if (StringUtils.isBlank(sql)) {
      if (LogCategory.SETS.contains(categoryName)) {
        log.info(
            "#{} [ {} ] - {}",
            "%04d".formatted(connectionId),
            "%9s".formatted(categoryName),
            switch (LogCategory.valueOf(categoryName)) {
              case COMMIT -> "Transaction committed";
              case ROLLBACK -> "Transaction rolled back";
              case BATCH -> "Batch executed";
              case OUTAGE -> "Connection outage occurred";
              default -> "...";
            });

        return;
      }

      super.logSQL(connectionId, time, elapsed, category, prepared, sql, url);

      return;
    }

    log.info(sql);
  }
}
