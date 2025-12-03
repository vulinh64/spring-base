package com.vulinh.service.event;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
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

  public void sendNewPostEvent(Post post, UserBasicResponse actionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newPostTopic(),
        actionUser,
        EVENT_MAPPER.toNewPostEvent(post));
  }

  public void sendSubscribeToUserEvent(
      UserBasicResponse basicActionUser, KeycloakUserResponse subscribedUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().subscribeToUserTopic(),
        basicActionUser,
        EVENT_MAPPER.toNewSubscriptionEvent(subscribedUser));
  }

  public void sendNewCommentEvent(Comment comment, Post post, UserBasicResponse basicActionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newCommentEventTopic(),
        basicActionUser,
        EVENT_MAPPER.toNewCommentEvent(comment, post));
  }

  private <T> void sendMessageInternal(
      String topic, UserBasicResponse basicActionUser, T eventData) {
    var eventMessage =
        EventMessageWrapper.of(EVENT_MAPPER.toActionUser(basicActionUser), eventData);

    streamBridge.send(topic, eventMessage);

    var actionUser = eventMessage.actionUser();

    log.debug(
        "Event ID [ {} ] | Action user [ {} ] - [ {} ] | Sent message {} to topic [ {} ] @ {}",
        eventMessage.getEventId(),
        actionUser.id(),
        actionUser.username(),
        JsonUtils.toMinimizedJSON(eventMessage.data()),
        topic,
        eventMessage.getTimestamp());
  }
}
