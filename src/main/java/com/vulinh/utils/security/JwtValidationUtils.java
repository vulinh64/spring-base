package com.vulinh.utils.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.security.JwtPayload;
import com.vulinh.exception.CustomSecurityException;
import com.vulinh.exception.ExceptionBuilder;
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
public class JwtValidationUtils {

  private static final ObjectMapper OBJECT_MAPPER = JsonUtils.delegate();

  public JwtPayload validate(String token) {
    try {
      var parsingResult = getJwtParser().parseClaimsJws(token).getBody();

      return OBJECT_MAPPER.convertValue(parsingResult, JwtPayload.class);
    } catch (ExpiredJwtException expiredJwtException) {
      throw new CustomSecurityException(
          "Credentials token expired",
          CommonMessage.MESSAGE_CREDENTIALS_EXPIRED,
          expiredJwtException);
    } catch (Exception exception) {
      throw ExceptionBuilder.invalidAuthorization(exception);
    }
  }

  private static JwtParser jwtParser;

  // Single initialization
  private static JwtParser getJwtParser() {
    try {
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
    } catch (Exception exception) {
      throw new DecodingException("Invalid public key");
    }
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
