package com.vulinh.configuration;

import com.vulinh.data.elasticsearch.ElasticsearchRootRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = ElasticsearchRootRepository.class)
@RequiredArgsConstructor
public class CustomElasticsearchConfiguration extends ElasticsearchConfiguration {

  private final ElasticsearchProperties elasticsearchConfigurationProperties;

  @Override
  @NonNull
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(elasticsearchConfigurationProperties.getUris().toArray(new String[0]))
        .build();
  }
}
