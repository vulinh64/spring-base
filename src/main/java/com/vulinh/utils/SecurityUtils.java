package com.vulinh.utils;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.security.CustomAuthentication;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.exception.CustomSecurityException;
import com.vulinh.exception.ExceptionBuilder;
import jakarta.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SecurityUtils {

  private static PrivateKey singletonPrivateKey;

  private static final KeyFactory KEY_FACTORY;

  static {
    try {
      KEY_FACTORY = KeyFactory.getInstance("RSA");
      log.info("Using RSA as default algorithm for KeyFactory");
    } catch (Exception exception) {
      throw new CustomSecurityException(
          "Initializing key factory failed",
          CommonMessage.MESSAGE_INVALID_CREDENTIALS_ISSUER,
          exception);
    }
  }

  private static final Set<String> PUBLIC_KEY_TO_BE_TRIMMED =
      Set.of(
          "-----BEGIN PUBLIC KEY-----",
          "-----END PUBLIC KEY-----",
          "-----BEGIN RSA PUBLIC KEY-----",
          "-----END RSA PUBLIC KEY-----");

  private static final Set<String> PRIVATE_KEY_TO_BE_TRIMMED =
      Set.of(
          "-----BEGIN PRIVATE KEY-----",
          "-----END PRIVATE KEY-----",
          "-----BEGIN RSA PRIVATE KEY-----",
          "-----END RSA PRIVATE KEY-----");

  @SneakyThrows
  public static PublicKey generatePublicKey(String rawPublicKey) {
    // Remove header and footer if they are present
    // Also remove any whitespace character (\n, \r, space)
    var refinedPrivateKey =
        StringUtils.deleteWhitespace(stripRawKey(rawPublicKey, PUBLIC_KEY_TO_BE_TRIMMED));

    return KEY_FACTORY.generatePublic(
        new X509EncodedKeySpec(Base64.getDecoder().decode(refinedPrivateKey)));
  }

  @SneakyThrows
  public static PrivateKey generatePrivateKey(String rawPrivateKey) {
    if (singletonPrivateKey == null) {
      // Remove header and footer if they are present
      // Also remove any whitespace character (\n, \r, space)
      var refinedPrivateKey =
          StringUtils.deleteWhitespace(stripRawKey(rawPrivateKey, PRIVATE_KEY_TO_BE_TRIMMED));

      singletonPrivateKey =
          KEY_FACTORY.generatePrivate(
              new PKCS8EncodedKeySpec(Base64.getDecoder().decode(refinedPrivateKey)));
    }

    return singletonPrivateKey;
  }

  public static Optional<UserBasicDTO> getUserDTO(@Nullable HttpServletRequest httpServletRequest) {
    return Optional.ofNullable(httpServletRequest)
        .map(HttpServletRequest::getUserPrincipal)
        .or(
            () ->
                Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication))
        .filter(CustomAuthentication.class::isInstance)
        .map(CustomAuthentication.class::cast)
        .map(CustomAuthentication::getPrincipal);
  }

  @NonNull
  public static UserBasicDTO getUserDTOOrThrow(HttpServletRequest httpServletRequest) {
    return getUserDTO(httpServletRequest).orElseThrow(ExceptionBuilder::invalidAuthorization);
  }

  private static String stripRawKey(String rawKey, Collection<String> toBeRemoved) {
    var result = rawKey;

    for (var removed : toBeRemoved) {
      result = result.replace(removed, StringUtils.EMPTY);
    }

    return result;
  }
}
