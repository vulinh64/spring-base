package com.vulinh.service.auth;

import com.vulinh.configuration.SecurityConfigProperties;
import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.RefreshTokenRequestDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.CommonException;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.UserRegistrationEventFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.service.auth.PasswordValidationService.PasswordChangeRule;
import com.vulinh.service.sessions.UserSessionService;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.security.AccessTokenGenerator;
import com.vulinh.utils.security.AccessTokenValidator;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.security.RefreshTokenValidator;
import com.vulinh.utils.validator.ValidatorChain;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private static final ValidatorStepFactory VALIDATOR_STEP_FACTORY = ValidatorStepFactory.INSTANCE;

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private final SecurityConfigProperties securityConfigProperties;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserValidationService userValidationService;
  private final PasswordValidationService passwordValidationService;
  private final UserSessionService userSessionService;

  private final AccessTokenValidator accessTokenValidator;
  private final RefreshTokenValidator refreshTokenValidator;
  private final AccessTokenGenerator accessTokenGenerator;

  private final ApplicationEventPublisher applicationEventPublisher;

  public TokenResponse login(UserLoginDTO userLoginDTO) {
    var now = Instant.now();

    return userRepository
        .findByUsernameAndIsActiveIsTrue(userLoginDTO.username())
        .filter(
            matchedUser ->
                UserValidationService.isPasswordMatched(userLoginDTO, matchedUser, passwordEncoder))
        .map(user -> accessTokenGenerator.generateAccessToken(user.getId(), UUID.randomUUID(), now))
        .map(userSessionService::createUserSession)
        .orElseThrow(
            () ->
                EXCEPTION_FACTORY.buildCommonException(
                    "Invalid user credentials", CommonMessage.MESSAGE_INVALID_CREDENTIALS));
  }

  // Normal user that requires confirmation
  @Transactional
  public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
    userValidationService.validateUserCreation(userRegistrationDTO);

    var registrationWithEncodedPassword =
        userRegistrationDTO.withPassword(passwordEncoder.encode(userRegistrationDTO.password()));

    var newUser =
        userRepository.save(
            USER_MAPPER.toUserWithRegistrationCode(
                registrationWithEncodedPassword, UUID.randomUUID().toString()));

    applicationEventPublisher.publishEvent(UserRegistrationEventFactory.INSTANCE.fromUser(newUser));

    return USER_MAPPER.toUserDTO(newUser);
  }

  // Confirm normal user
  @Transactional
  public boolean confirmUser(UUID userId, String code) {
    if (userId == null || StringUtils.isBlank(code)) {
      return false;
    }

    return userRepository
        .findById(userId)
        .filter(matchedUser -> UserValidationService.isRegistrationCodeMatched(code, matchedUser))
        .filter(Predicate.not(UserValidationService::isActive))
        .map(matchedUser -> matchedUser.setIsActive(true).setUserRegistrationCode(null))
        .map(userRepository::save)
        .isPresent();
  }

  @Transactional
  public void changePassword(
      PasswordChangeDTO passwordChangeDTO, HttpServletRequest httpServletRequest) {
    ValidatorChain.<PasswordChangeDTO>start()
        .addValidator(PasswordChangeRule.values())
        .executeValidation(passwordChangeDTO);

    var userEntity =
        SecurityUtils.getUserDTO(httpServletRequest)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(EXCEPTION_FACTORY::invalidAuthorization);

    ValidatorChain.<PasswordChangeDTO>start()
        .addValidator(
            VALIDATOR_STEP_FACTORY.build(
                passwordValidationService.isOldPasswordMatched(userEntity),
                CommonMessage.MESSAGE_PASSWORD_MISMATCHED,
                "Invalid old password"),
            VALIDATOR_STEP_FACTORY.build(
                passwordValidationService.isNewPasswordNotMatched(userEntity),
                CommonMessage.MESSAGE_SAME_OLD_PASSWORD,
                "New password cannot be the same as old password"))
        .executeValidation(passwordChangeDTO);

    userRepository.save(
        userEntity.setPassword(passwordEncoder.encode(passwordChangeDTO.newPassword())));
  }

  // TODO: Use ID to check the existence of entities
  @Transactional
  public TokenResponse refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
    var refreshToken = refreshTokenRequestDTO.refreshToken();

    if (StringUtils.isBlank(refreshToken)) {
      throw EXCEPTION_FACTORY.buildCommonException(
          "Empty refresh token", CommonMessage.MESSAGE_INVALID_TOKEN);
    }

    var decodedJwtPayload = refreshTokenValidator.validateRefreshToken(refreshToken);

    var user = userRepository.findActiveUser(decodedJwtPayload.userId());

    var userSession =
        userSessionService.findUserSession(user.getId(), decodedJwtPayload.sessionId());

    var issuedAt = Instant.now();

    userSessionService.updateUserSession(
        userSession, issuedAt.plus(securityConfigProperties.refreshJwtDuration()));

    var tokenResult =
        accessTokenGenerator
            .generateAccessToken(
                decodedJwtPayload.userId(), decodedJwtPayload.sessionId(), issuedAt)
            .tokenResponse();

    return TokenResponse.builder()
        .accessToken(tokenResult.accessToken())
        .refreshToken(tokenResult.refreshToken())
        .build();
  }

  // BAD, yes, I am validating the JWT directly, but I am too lazy
  // to refactor the validation utils, who cares?
  @Transactional
  public boolean logout(String authorization) {
    try {
      var jwtPayload =
          accessTokenValidator.validateAccessToken(Auth0Utils.parseBearerToken(authorization));

      var userId = jwtPayload.userId();

      if (!userRepository.existsByIdAndIsActiveIsTrue(userId)) {
        log.info("User ID {} did not exist", userId);
        return false;
      }

      var userSession = userSessionService.findUserSession(userId, jwtPayload.sessionId());

      userSessionService.deleteUserSession(userSession);

      return true;
    } catch (CommonException commonException) {
      switch (commonException.getErrorKey()) {
        case CommonMessage.MESSAGE_INVALID_TOKEN ->
            // Token format error
            throw commonException;

        case CommonMessage.MESSAGE_INVALID_PUBLIC_KEY_CONFIG ->
            // Invalid public key
            throw ExceptionFactory.INSTANCE.parsingPublicKeyError(commonException);
        default -> {
          log.info(commonException.getMessage());
          return false;
        }
      }
    }
  }
}
