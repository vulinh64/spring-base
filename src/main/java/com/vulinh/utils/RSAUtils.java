package com.vulinh.utils;

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
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for handling RSA key operations.
 *
 * <p>This class provides methods to generate {@link RSAPublicKey} and {@link RSAPrivateKey}
 * instances from their string representations. It can handle raw Base64 encoded keys as well as
 * keys in PEM format (with headers, footers, and line breaks).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RSAUtils {

  private static final KeyFactory RSA_KEY_FACTORY;

  static {
    try {
      RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      // Should never happen in a proper JDK
      throw new ExceptionInInitializerError(e);
    }
  }

  private static final String[] PUBLIC_PEM_HEADER_FOOTER = {
    "-----BEGIN PUBLIC KEY-----",
    "-----END PUBLIC KEY-----",
    "-----BEGIN RSA PUBLIC KEY-----",
    "-----END RSA PUBLIC KEY-----"
  };

  private static final String[] PRIVATE_PEM_HEADER_FOOTER = {
    "-----BEGIN PRIVATE KEY-----",
    "-----END PRIVATE KEY-----",
    "-----BEGIN RSA PRIVATE KEY-----",
    "-----END RSA PRIVATE KEY-----"
  };

  /**
   * Generates an {@link RSAPublicKey} from its string representation.
   *
   * <p>This method takes a raw public key string, which can be in PEM format (e.g., containing
   * "-----BEGIN PUBLIC KEY-----" headers/footers) or a simple Base64 encoded string. It strips any
   * PEM headers/footers and whitespace before decoding and generating the key.
   *
   * @param rawPublicKey The string representation of the public key.
   * @return The generated {@link RSAPublicKey}.
   * @throws SecurityConfigurationException if the provided key string is invalid, malformed, or not
   *     an RSA public key.
   */
  public static RSAPublicKey generatePublicKey(String rawPublicKey) {
    // Remove header and footer if they are present
    // Also remove any whitespace character (\n, \r, space)
    var refinedPublicKey =
        StringUtils.deleteWhitespace(stripRawKey(rawPublicKey, PUBLIC_PEM_HEADER_FOOTER));

    if (!(generateRSAPublicKey(refinedPublicKey) instanceof RSAPublicKey rsaPublicKey)) {
      throw SecurityConfigurationException.configurationException(
          "Not an instance of RSAPublicKey", ServiceErrorCode.MESSAGE_INVALID_PUBLIC_KEY_CONFIG);
    }

    return rsaPublicKey;
  }

  /**
   * Generates an {@link RSAPrivateKey} from its string representation.
   *
   * <p>This method takes a raw private key string, which can be in PEM format (e.g., containing
   * "-----BEGIN PRIVATE KEY-----" headers/footers) or a simple Base64 encoded string. It strips any
   * PEM headers/footers and whitespace before decoding and generating the key.
   *
   * @param rawPrivateKey The string representation of the private key.
   * @return The generated {@link RSAPrivateKey}.
   * @throws SecurityConfigurationException if the provided key string is invalid, malformed, or not
   *     an RSA private key.
   */
  public static RSAPrivateKey generatePrivateKey(String rawPrivateKey) {
    // Remove header and footer if they are present
    // Also remove any whitespace character (\n, \r, space)
    var refinedPrivateKey =
        StringUtils.deleteWhitespace(stripRawKey(rawPrivateKey, PRIVATE_PEM_HEADER_FOOTER));

    if (!(generateRSAPrivateKey(refinedPrivateKey) instanceof RSAPrivateKey rsaPrivateKey)) {
      throw SecurityConfigurationException.configurationException(
          "Not an instance of RSAPrivateKey", ServiceErrorCode.MESSAGE_INVALID_PRIVATE_KEY_CONFIG);
    }

    return rsaPrivateKey;
  }

  private static String stripRawKey(String rawKey, String[] partsToRemove) {
    var atomicCounter = new AtomicInteger(0);

    return rawKey
        .replace(partsToRemove[atomicCounter.getAndIncrement()], StringUtils.EMPTY)
        .replace(partsToRemove[atomicCounter.getAndIncrement()], StringUtils.EMPTY)
        .replace(partsToRemove[atomicCounter.getAndIncrement()], StringUtils.EMPTY)
        .replace(partsToRemove[atomicCounter.getAndIncrement()], StringUtils.EMPTY);
  }

  private static PublicKey generateRSAPublicKey(String refinedPublicKey) {
    try {
      return RSA_KEY_FACTORY.generatePublic(
          new X509EncodedKeySpec(Base64.getDecoder().decode(refinedPublicKey)));
    } catch (Exception exception) {
      throw SecurityConfigurationException.configurationException(
          "Invalid public key configuration",
          ServiceErrorCode.MESSAGE_INVALID_PUBLIC_KEY_CONFIG,
          exception);
    }
  }

  private static PrivateKey generateRSAPrivateKey(String refinedPrivateKey) {
    try {
      return RSA_KEY_FACTORY.generatePrivate(
          new PKCS8EncodedKeySpec(Base64.getDecoder().decode(refinedPrivateKey)));
    } catch (Exception exception) {
      throw SecurityConfigurationException.configurationException(
          "Invalid private key configuration",
          ServiceErrorCode.MESSAGE_INVALID_PRIVATE_KEY_CONFIG,
          exception);
    }
  }
}
