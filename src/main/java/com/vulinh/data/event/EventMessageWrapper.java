package com.vulinh.data.event;

import com.vulinh.configuration.data.ApplicationProperties.TopicProperties;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record EventMessageWrapper<T, U extends BaseActionUser>(
    EventType eventType, U actionUser, T data) implements BaseEvent {

  public static <T, U extends BaseActionUser> EventMessageWrapper<T, U> of(
      TopicProperties topicProperties, U basicActionUser, T data) {
    return EventMessageWrapper.<T, U>builder()
        .eventType(topicProperties.type())
        .actionUser(basicActionUser)
        .data(data)
        .build();
  }
}
