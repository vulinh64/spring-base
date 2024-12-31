package com.vulinh.configuration;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.jwt")
public record SecurityConfigProperties(
    String publicKey,
    String privateKey,
    List<String> noAuthenticatedUrls,
    List<VerbUrl> allowedVerbUrls,
    Duration jwtDuration,
    Duration passwordResetCodeDuration,
    String issuer) {}
