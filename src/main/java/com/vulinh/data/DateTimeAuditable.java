package com.vulinh.data;

import java.time.LocalDateTime;

public interface DateTimeAuditable {

  LocalDateTime getCreatedDate();

  LocalDateTime getUpdatedDate();
}
