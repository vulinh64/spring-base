package com.vulinh.configuration.data;

import module java.base;

import com.vulinh.data.config.HttpMethodUrl;
import com.vulinh.data.config.RecordPublicSecurityPath;
import com.vulinh.data.event.EventType;
import com.vulinh.utils.CollectionHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application-properties")
public record ApplicationProperties(SecurityProperties security, MessageTopic messageTopic) {

  public record SecurityProperties(
      List<String> noAuthUrls,
      List<HttpMethodUrl> noAuthMethodUrls,
      List<HttpMethodUrl> highPrivilegeVerbUrls,
      String issuerUri,
      String jwkSetUri,
      String clientName)
      implements RecordPublicSecurityPath {

    public SecurityProperties {
      noAuthUrls = CollectionHelper.emptyListIfNull(noAuthUrls);
      noAuthMethodUrls = CollectionHelper.emptyListIfNull(noAuthMethodUrls);
      highPrivilegeVerbUrls = CollectionHelper.emptyListIfNull(highPrivilegeVerbUrls);
    }
  }

  public record MessageTopic(
      TopicProperties newPost,
      TopicProperties newSubscriber,
      TopicProperties newComment,
      TopicProperties newPostFollowing) {}

  public record TopicProperties(EventType type, String topicName) {}
}
