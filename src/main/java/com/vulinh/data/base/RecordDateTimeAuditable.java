package com.vulinh.data.base;

import java.time.temporal.TemporalAccessor;

public interface RecordDateTimeAuditable extends DateTimeAuditable {

  TemporalAccessor createdDate();

  TemporalAccessor updatedDate();

  @Override
  default TemporalAccessor getCreatedDate() {
    return createdDate();
  }

  @Override
  default TemporalAccessor getUpdatedDate() {
    return updatedDate();
  }
}
