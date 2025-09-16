package com.vulinh.data.base;

import module java.base;

public interface DateTimeAuditable {

  TemporalAccessor getCreatedDate();

  TemporalAccessor getUpdatedDate();
}
