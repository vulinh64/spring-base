package com.vulinh.configuration;

import com.vulinh.data.elasticsearch.ElasticsearchRootRepository;
import java.net.InetSocketAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableConfigurationProperties(ElasticsearchConfigurationProperties.class)
@EnableElasticsearchRepositories(basePackageClasses = ElasticsearchRootRepository.class)
@RequiredArgsConstructor
public class CustomElasticsearchConfiguration extends ElasticsearchConfiguration {

  private final ElasticsearchConfigurationProperties elasticsearchConfigurationProperties;

  @Override
  @NonNull
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(
            InetSocketAddress.createUnresolved(
                elasticsearchConfigurationProperties.host(),
                elasticsearchConfigurationProperties.port()))
        .build();
  }
}
