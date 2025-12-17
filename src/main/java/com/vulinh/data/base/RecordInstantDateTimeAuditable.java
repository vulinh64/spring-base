package com.vulinh.data.base;

import module java.base;

public interface RecordInstantDateTimeAuditable extends InstantDateTimeAuditable {

  Instant createdDateTime();

  Instant updatedDateTime();

  @Override
  default Instant getCreatedDateTime() {
    return createdDateTime();
  }

  @Override
  default Instant getUpdatedDateTime() {
    return updatedDateTime();
  }
}
