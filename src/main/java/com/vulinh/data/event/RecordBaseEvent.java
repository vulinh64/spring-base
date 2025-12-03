package com.vulinh.data.event;

public interface RecordBaseEvent extends BaseEvent {

  @Override
  default BaseActionUser getActionUser() {
    return actionUser();
  }

  BaseActionUser actionUser();
}
