package com.vulinh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.configuration.data.ApplicationProperties.TopicProperties;
import com.vulinh.data.constant.UserRole;
import com.vulinh.data.event.EventMessageWrapper;
import com.vulinh.utils.JsonUtils;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.core.type.TypeReference;

@SpringBootTest
@ActiveProfiles("development")
@AutoConfigureMockMvc
@Import(ContainersConfiguration.class)
public abstract class AbstractBaseIntegration {

  protected static final UUID USER_ID = UUID.fromString("b947e52c-d0b7-4c1d-9d62-3af33b2d968f");

  protected static final String USERNAME = "integration-test-user";

  protected static final UUID EXISTING_POST_ID =
      UUID.fromString("c0a802df-9fe5-1c2a-819f-e5ec41120003");

  protected static final long EVENT_RECEIVE_TIMEOUT_MILLIS = 10_000;

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ApplicationProperties applicationProperties;

  // False positive
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  protected RabbitAdmin rabbitAdmin;

  @Autowired protected RabbitTemplate rabbitTemplate;

  @Autowired protected JdbcTemplate jdbcTemplate;

  protected RequestPostProcessor testUserJwt() {
    return jwt()
        .jwt(
            token ->
                token
                    .subject(USER_ID.toString())
                    .claim("username", USERNAME)
                    .claim("roles", java.util.List.of(UserRole.USER.name())))
        .authorities(new SimpleGrantedAuthority(UserRole.USER.name()));
  }

  protected Queue initializeRabbitMqQueue(TopicProperties topic, String queueNamePrefix) {
    var exchange = new TopicExchange(topic.topicName());
    var queue = new Queue("%s%s".formatted(queueNamePrefix, UUID.randomUUID()), false, true, true);

    rabbitAdmin.declareExchange(exchange);
    rabbitAdmin.declareQueue(queue);
    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("#"));

    return queue;
  }

  protected <T> EventMessageWrapper<T> receiveEvent(
      Queue queue, TypeReference<EventMessageWrapper<T>> eventType) {
    var message = rabbitTemplate.receive(queue.getName(), EVENT_RECEIVE_TIMEOUT_MILLIS);
    assertNotNull(message, "Expected the event to reach RabbitMQ");

    return JsonUtils.toObject(new String(message.getBody(), StandardCharsets.UTF_8), eventType);
  }

  protected void assertTestUserAction(EventMessageWrapper<?> event) {
    assertEquals(USER_ID, event.actionUser().id());
    assertEquals(USERNAME, event.actionUser().username());
  }
}
