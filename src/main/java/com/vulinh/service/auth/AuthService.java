package com.vulinh.service.auth;

import module java.base;

import com.vulinh.configuration.ApplicationProperties;
import com.vulinh.data.dto.carrier.TokenResponse;
import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.dto.request.RefreshTokenRequest;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.AuthorizationException;
import com.vulinh.exception.InvalidCredentialsException;
import com.vulinh.exception.ValidationException;
import com.vulinh.factory.UserRegistrationEventFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.auth.PasswordValidationService.PasswordChangeRule;
import com.vulinh.service.sessions.UserSessionService;
import com.vulinh.service.token.AccessTokenGenerator;
import com.vulinh.service.token.AccessTokenValidator;
import com.vulinh.service.token.RefreshTokenValidator;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.security.Auth0Utils;
import com.vulinh.utils.validator.ValidatorChain;
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

  static final ValidatorStepFactory VALIDATOR_STEP_FACTORY = ValidatorStepFactory.INSTANCE;

  static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  final ApplicationProperties applicationProperties;

  final UserRepository userRepository;

  final PasswordEncoder passwordEncoder;

  final UserValidationService userValidationService;
  final PasswordValidationService passwordValidationService;
  final UserSessionService userSessionService;

  final AccessTokenValidator accessTokenValidator;
  final RefreshTokenValidator refreshTokenValidator;
  final AccessTokenGenerator accessTokenGenerator;

  final ApplicationEventPublisher applicationEventPublisher;

  public TokenResponse login(UserLoginRequest userLoginRequest) {
    var now = Instant.now();

    return userRepository
        .findByUsernameAndIsActiveIsTrue(userLoginRequest.username())
        .filter(
            matchedUser ->
                UserValidationService.isPasswordMatched(
                    userLoginRequest, matchedUser, passwordEncoder))
        .map(user -> accessTokenGenerator.generateAccessToken(user.getId(), UUID.randomUUID(), now))
        .map(userSessionService::createUserSession)
        .orElseThrow(InvalidCredentialsException::invalidCredentialsException);
  }

  // Normal user that requires confirmation
  @Transactional
  public SingleUserResponse registerUser(UserRegistrationRequest userRegistrationRequest) {
    userValidationService.validateUserCreation(userRegistrationRequest);

    var registrationWithEncodedPassword =
        userRegistrationRequest.withPassword(
            passwordEncoder.encode(userRegistrationRequest.password()));

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
        .filter(Predicate.not(UserValidationService::isActive))
        .filter(matchedUser -> UserValidationService.isRegistrationCodeMatched(code, matchedUser))
        .map(matchedUser -> matchedUser.setIsActive(true).setUserRegistrationCode(null))
        .map(userRepository::save)
        .isPresent();
  }

  @Transactional
  public void changePassword(PasswordChangeRequest passwordChangeRequest) {
    ValidatorChain.<PasswordChangeRequest>start()
        .addValidator(PasswordChangeRule.values())
        .executeValidation(passwordChangeRequest);

    var userEntity =
        SecurityUtils.getUserDTO()
            .map(UserBasicResponse::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(AuthorizationException::invalidAuthorization);

    ValidatorChain.<PasswordChangeRequest>start()
        .addValidator(
            VALIDATOR_STEP_FACTORY.build(
                passwordValidationService.isOldPasswordMatched(userEntity),
                ServiceErrorCode.MESSAGE_PASSWORD_MISMATCHED,
                "Invalid old password"),
            VALIDATOR_STEP_FACTORY.build(
                passwordValidationService.isNewPasswordNotMatched(userEntity),
                ServiceErrorCode.MESSAGE_SAME_OLD_PASSWORD,
                "New password cannot be the same as old password"))
        .executeValidation(passwordChangeRequest);

    userRepository.save(
        userEntity.setPassword(passwordEncoder.encode(passwordChangeRequest.newPassword())));
  }

  // TODO: Use ID to check the existence of entities
  @Transactional
  public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    var refreshToken = refreshTokenRequest.refreshToken();

    if (StringUtils.isBlank(refreshToken)) {
      throw ValidationException.validationException(
          "Empty refresh token", ServiceErrorCode.MESSAGE_INVALID_TOKEN);
    }

    var decodedJwtPayload = refreshTokenValidator.validateRefreshToken(refreshToken);

    var user = userRepository.findActiveUser(decodedJwtPayload.userId());

    var userSession =
        userSessionService.findUserSession(user.getId(), decodedJwtPayload.sessionId());

    var issuedAt = Instant.now();

    userSessionService.updateUserSession(
        userSession, issuedAt.plus(applicationProperties.security().refreshJwtDuration()));

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
  }
}
