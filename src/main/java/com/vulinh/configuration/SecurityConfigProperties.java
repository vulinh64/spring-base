package com.vulinh.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "spring.security.jwt")
public record SecurityConfigProperties(
    String publicKey,
    String privateKey,
    List<String> noAuthenticatedUrls,
    List<VerbUrl> allowedVerbUrls,
    Duration jwtDuration,
    Duration passwordResetCodeDuration,
    String issuer) {}
