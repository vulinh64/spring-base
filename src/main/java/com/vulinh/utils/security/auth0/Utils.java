package com.vulinh.utils.security.auth0;

import com.auth0.jwt.algorithms.Algorithm;
import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.entity.ids.QUserSessionId;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

  static final String USER_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.userId);

  static final String SESSION_ID_CLAIM =
      PredicateBuilder.getFieldName(QUserSessionId.userSessionId.sessionId);

  private static Algorithm rsaAlgorithm;

  static Algorithm getAlgorithm(SecurityConfigProperties securityConfigProperties) {
    if (rsaAlgorithm == null) {
      rsaAlgorithm =
          Algorithm.RSA512(
              SecurityUtils.generatePublicKey(securityConfigProperties.publicKey()),
              SecurityUtils.generatePrivateKey(securityConfigProperties.privateKey()));
    }

    return rsaAlgorithm;
  }
}
