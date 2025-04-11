package com.vulinh.configuration.data;

import com.vulinh.configuration.VerbUrl;
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
    Duration refreshJwtDuration,
    Duration passwordResetCodeDuration,
    String issuer) {}
