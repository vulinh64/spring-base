package com.vulinh.service.event;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.entity.Post;
import com.vulinh.data.event.BaseEvent;
import com.vulinh.data.mapper.EventMapper;
import com.vulinh.utils.SecurityUtils;
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

  final StreamBridge streamBridge;

  final ApplicationProperties applicationProperties;

  public void sendNewPostEvent(Post post) {
    sendMessageInternal(
        applicationProperties.messageTopic().newPostTopic(),
        EventMapper.INSTANCE.toNewPostEvent(post, SecurityUtils.getUserDTOOrThrow()));
  }

  private <T extends BaseEvent> void sendMessageInternal(String topic, T event) {
    log.debug("Sending message {} to topic [{}] @ {}...", event, topic, event.timestamp());

    streamBridge.send(topic, event);
  }
}
