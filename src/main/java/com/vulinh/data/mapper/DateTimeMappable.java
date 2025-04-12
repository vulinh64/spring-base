package com.vulinh.data.mapper;

import com.vulinh.utils.DateTimeUtils;
import java.time.Instant;
import java.time.LocalDateTime;

public interface DateTimeMappable {

  default LocalDateTime toLocalDateTime(Instant temporalAccessor) {
    return DateTimeUtils.toLocalDateTime(temporalAccessor);
  }

  default Instant toInstant(LocalDateTime temporalAccessor) {
    return DateTimeUtils.toInstant(temporalAccessor);
  }
}
