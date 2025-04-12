package com.vulinh.data.base;

import java.time.temporal.TemporalAccessor;

public interface DateTimeAuditable {

  TemporalAccessor getCreatedDate();

  TemporalAccessor getUpdatedDate();
}
