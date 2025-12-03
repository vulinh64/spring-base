package com.vulinh.data.event;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record EventMessageWrapper<T, U extends BaseActionUser>(U actionUser, T data)
    implements BaseEvent {

  public static <T, U extends BaseActionUser> EventMessageWrapper<T, U> of(
      U basicActionUser, T data) {
    return EventMessageWrapper.<T, U>builder().actionUser(basicActionUser).data(data).build();
  }
}
