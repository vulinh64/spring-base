package com.vulinh.service.event;

import module java.base;

import com.vulinh.annotation.ExecutionTime;
import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.configuration.data.ApplicationProperties.TopicProperties;
import com.vulinh.data.dto.response.KeycloakUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.EventMessageWrapper;
import com.vulinh.data.mapper.EventMapper;
import com.vulinh.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Async
public class EventService {

  static final EventMapper EVENT_MAPPER = EventMapper.INSTANCE;

  final StreamBridge streamBridge;

  final ApplicationProperties applicationProperties;

  @ExecutionTime
  public void sendNewPostEvent(Post post, UserBasicResponse actionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newPost(),
        actionUser,
        EVENT_MAPPER.toNewPostEvent(post));
  }

  @ExecutionTime
  public void sendSubscribeToUserEvent(
      UserBasicResponse basicActionUser, KeycloakUserResponse subscribedUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newSubscriber(),
        basicActionUser,
        EVENT_MAPPER.toNewSubscriptionEvent(subscribedUser));
  }

  @ExecutionTime
  public void sendNewCommentEvent(Comment comment, Post post, UserBasicResponse basicActionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newComment(),
        basicActionUser,
        EVENT_MAPPER.toNewCommentEvent(comment, post));
  }

  @ExecutionTime
  public void sendNewPostFollowingEvent(Post post, UserBasicResponse basicActionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newPostFollowing(),
        basicActionUser,
        EVENT_MAPPER.toNewPostFollowingEvent(post));
  }

  private <T> void sendMessageInternal(
      TopicProperties topic, UserBasicResponse basicActionUser, T eventData) {
    var eventMessage = of(topic, basicActionUser, eventData);

    streamBridge.send(topic.topicName(), eventMessage);

    var actionUser = eventMessage.actionUser();

    log.debug(
        "Event ID [ {} ] | Type [ {} ] | Action user [ {} ] - [ {} ] | Sent message {} to topic [ {} ] @ {}",
        eventMessage.eventId(),
        topic.type(),
        actionUser.id(),
        actionUser.username(),
        JsonUtils.toMinimizedJSON(eventMessage.data()),
        topic.topicName(),
        eventMessage.timestamp());
  }

  private <T> EventMessageWrapper<T> of(
      TopicProperties topic, UserBasicResponse basicActionUser, T eventData) {
    return EventMessageWrapper.<T>builder()
        .eventType(topic.type())
        .actionUser(EVENT_MAPPER.toActionUser(basicActionUser))
        .data(eventData)
        .build();
  }
}
