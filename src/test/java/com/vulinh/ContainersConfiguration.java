package com.vulinh;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration {

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresqlContainer() {
    return new PostgreSQLContainer("postgres:18.3-alpine3.23");
  }

  @Bean
  @ServiceConnection
  RabbitMQContainer rabbitmqContainer() {
    return new RabbitMQContainer("rabbitmq:4.2.4-alpine");
  }
}
