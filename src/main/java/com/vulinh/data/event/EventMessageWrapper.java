package com.vulinh.data.event;

import com.vulinh.configuration.data.ApplicationProperties.TopicProperties;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.mapper.EventMapper;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record EventMessageWrapper<T>(EventType eventType, ActionUser actionUser, T data)
    implements BaseEvent {

  public static <T> EventMessageWrapper<T> of(
      TopicProperties topicProperties, UserBasicResponse basicActionUser, T data) {
    return EventMessageWrapper.<T>builder()
        .eventType(topicProperties.type())
        .actionUser(EventMapper.INSTANCE.toActionUser(basicActionUser))
        .data(data)
        .build();
  }
}
