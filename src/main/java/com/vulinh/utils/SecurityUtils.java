package com.vulinh.utils;

import com.vulinh.configuration.CustomAuthentication;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.exception.SecurityConfigurationException;
import com.vulinh.locale.ServiceErrorCode;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

  private static RSAPrivateKey singletonPrivateKey;

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
  public static RSAPublicKey generatePublicKey(String rawPublicKey) {
    // Remove header and footer if they are present
    // Also remove any whitespace character (\n, \r, space)
    var refinedPublicKey =
        StringUtils.deleteWhitespace(stripRawKey(rawPublicKey, PUBLIC_KEY_TO_BE_TRIMMED));

    var result = generateRSAPublicKey(refinedPublicKey);

    if (!(result instanceof RSAPublicKey rsaPublicKey)) {
      throw SecurityConfigurationException.configurationException(
          "Not an instance of RSAPublicKey", ServiceErrorCode.MESSAGE_INVALID_PUBLIC_KEY_CONFIG);
    }

    return rsaPublicKey;
  }

  @SneakyThrows
  public static RSAPrivateKey generatePrivateKey(String rawPrivateKey) {
    if (singletonPrivateKey == null) {
      // Remove header and footer if they are present
      // Also remove any whitespace character (\n, \r, space)
      var refinedPrivateKey =
          StringUtils.deleteWhitespace(stripRawKey(rawPrivateKey, PRIVATE_KEY_TO_BE_TRIMMED));

      var result = generateRSAPrivateKey(refinedPrivateKey);

      if (!(result instanceof RSAPrivateKey rsaPrivateKey)) {
        throw SecurityConfigurationException.configurationException(
            "Not an instance of RSAPrivateKey", ServiceErrorCode.MESSAGE_INVALID_PRIVATE_KEY_CONFIG);
      }

      singletonPrivateKey = rsaPrivateKey;
    }

    return singletonPrivateKey;
  }

  public static Optional<UserBasicResponse> getUserDTO() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .filter(CustomAuthentication.class::isInstance)
        .map(CustomAuthentication.class::cast)
        .map(CustomAuthentication::getPrincipal);
  }

  @NonNull
  public static UserBasicResponse getUserDTOOrThrow() {
    return getUserDTO().orElseThrow(AuthorizationException::invalidAuthorization);
  }

  private static String stripRawKey(String rawKey, Collection<String> toBeRemoved) {
    var result = rawKey;

    for (var removed : toBeRemoved) {
      result = result.replace(removed, StringUtils.EMPTY);
    }

    return result;
  }

  private static KeyFactory getRSAKeyFactoryInstance() throws NoSuchAlgorithmException {
    return KeyFactory.getInstance("RSA");
  }

  private static PublicKey generateRSAPublicKey(String refinedPrivateKey) {
    try {
      return getRSAKeyFactoryInstance()
          .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(refinedPrivateKey)));
    } catch (Exception exception) {
      throw SecurityConfigurationException.configurationException(
          "Invalid public key configuration",
          ServiceErrorCode.MESSAGE_INVALID_PUBLIC_KEY_CONFIG,
          exception);
    }
  }

  private static PrivateKey generateRSAPrivateKey(String refinedPrivateKey) {
    try {
      return getRSAKeyFactoryInstance()
          .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(refinedPrivateKey)));
    } catch (Exception exception) {
      throw SecurityConfigurationException.configurationException(
          "Invalid private key configuration",
          ServiceErrorCode.MESSAGE_INVALID_PRIVATE_KEY_CONFIG,
          exception);
    }
  }
}
