package com.vulinh.data.base;

import java.time.LocalDateTime;

public interface DateTimeAuditable {

  LocalDateTime getCreatedDate();

  LocalDateTime getUpdatedDate();
}
