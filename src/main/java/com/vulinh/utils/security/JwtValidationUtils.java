package com.vulinh.utils.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.utils.JsonUtils;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.StaticContextAccessor;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtValidationUtils implements AccessTokenValidator {

  private static final ObjectMapper OBJECT_MAPPER = JsonUtils.delegate();

  @Override
  public JwtPayload validateAccessToken(String accessToken) {
    try {
      var parsingResult = getJwtParser().parseClaimsJws(accessToken).getBody();

      return OBJECT_MAPPER.convertValue(parsingResult, JwtPayload.class);
    } catch (ExpiredJwtException expiredJwtException) {
      throw ExceptionFactory.INSTANCE.expiredAccessToken(expiredJwtException);
    }
  }

  private static JwtParser jwtParser;

  // Single initialization
  private static JwtParser getJwtParser() {
    if (jwtParser == null) {
      var properties = StaticContextAccessor.getBean(SecurityConfigProperties.class);

      var publicKey = SecurityUtils.generatePublicKey(properties.publicKey());

      jwtParser =
          Jwts.parserBuilder()
              .setSigningKey(publicKey)
              .deserializeJsonWith(JwtValidationUtils::deserialize)
              .build();
    }

    return jwtParser;
  }

  private static Map<String, ?> deserialize(byte[] json) {
    try {
      return OBJECT_MAPPER.readValue(json, new TypeReference<>() {});
    } catch (Exception ioException) {
      log.info("Invalid byte data", ioException);
      throw new DecodingException(null);
    }
  }
}
