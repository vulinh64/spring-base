package com.vulinh.service.event;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.entity.Post;
import com.vulinh.data.mapper.EventMapper;
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
    var newPostTopic = applicationProperties.messageTopic().newPostTopic();

    var newPostEvent = EventMapper.INSTANCE.toNewPostEvent(post);

    log.debug("Sending message {} to topic [{}]...", newPostEvent, newPostTopic);

    streamBridge.send(newPostTopic, newPostEvent);
  }
}
