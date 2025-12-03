package com.vulinh.service.event;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.dto.response.KeycloakUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.Comment;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.BaseEvent;
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
        EVENT_MAPPER.toNewPostEvent(post, actionUser));
  }

  public void sendSubscribeToUserEvent(
      UserBasicResponse actionUser, KeycloakUserResponse subscribedUserId) {
    sendMessageInternal(
        applicationProperties.messageTopic().subscribeToUserTopic(),
        EVENT_MAPPER.toNewSubscriptionEvent(actionUser, subscribedUserId));
  }

  public void sendNewCommentEvent(Comment comment, Post post, UserBasicResponse actionUser) {
    sendMessageInternal(
        applicationProperties.messageTopic().newCommentEventTopic(),
        EVENT_MAPPER.toNewCommentEvent(comment, post, actionUser));
  }

  private <T extends BaseEvent> void sendMessageInternal(String topic, T payload) {
    streamBridge.send(topic, payload);

    log.debug(
        "Action user [ {} ] - [ {} ] | Sent message [ {} ] to topic [ {} ] @ {}...",
        payload.actionUserId(),
        payload.actionUsername(),
        JsonUtils.toMinimizedJSON(payload),
        topic,
        payload.timestamp());
  }
}
