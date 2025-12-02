package com.vulinh.configuration;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.utils.JsonUtils;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {

  // Sensitive information, only means for internal use
  @Bean
  public Keycloak keycloak(ApplicationProperties applicationProperties) {
    var kcAuth = applicationProperties.keycloakAuthentication();

    return KeycloakBuilder.builder()
        .resteasyClient(new ResteasyClientBuilderImpl().register(JsonUtils.delegate(), 100).build())
        .serverUrl(kcAuth.authServer())
        .clientId(kcAuth.clientId())
        .realm(kcAuth.realm())
        .username(kcAuth.username())
        .password(kcAuth.password())
        .build();
  }
}
