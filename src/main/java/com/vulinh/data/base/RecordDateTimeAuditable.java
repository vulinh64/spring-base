package com.vulinh.data.base;

import java.time.LocalDateTime;

public interface RecordDateTimeAuditable extends DateTimeAuditable {

  LocalDateTime createdDate();

  LocalDateTime updatedDate();

  @Override
  default LocalDateTime getCreatedDate() {
    return createdDate();
  }

  @Override
  default LocalDateTime getUpdatedDate() {
    return updatedDate();
  }
}
