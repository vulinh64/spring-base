package com.vulinh.configuration;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.keycloak.KeycloakAuthExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfiguration {

  @Bean
  public KeycloakAuthExchange keycloakAuth(ApplicationProperties applicationProperties) {
    return HttpServiceProxyFactory.builderFor(
            RestClientAdapter.create(
                RestClient.builder().baseUrl(applicationProperties.security().issuerUri()).build()))
        .build()
        .createClient(KeycloakAuthExchange.class);
  }
}
