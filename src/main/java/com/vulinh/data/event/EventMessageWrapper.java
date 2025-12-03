package com.vulinh.data.event;

import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.mapper.EventMapper;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record EventMessageWrapper<T>(ActionUser actionUser, T data) implements BaseEvent {

  public static <T> EventMessageWrapper<T> of(UserBasicResponse basicActionUser, T data) {
    return new EventMessageWrapper<>(EventMapper.INSTANCE.toActionUser(basicActionUser), data);
  }
}
