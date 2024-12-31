package com.vulinh.service.auth;

import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.security.TokenResponse;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.UserRegistrationEventFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.service.auth.PasswordValidationService.PasswordChangeRule;
import com.vulinh.service.sessions.UserSessionService;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.security.AccessTokenGenerator;
import com.vulinh.utils.validator.ValidatorChain;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final ValidatorStepFactory VALIDATOR_STEP_FACTORY = ValidatorStepFactory.INSTANCE;

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserValidationService userValidationService;
  private final PasswordValidationService passwordValidationService;
  private final UserSessionService userSessionService;

  private final ApplicationEventPublisher applicationEventPublisher;

  private final AccessTokenGenerator accessTokenGenerator;

  public TokenResponse login(UserLoginDTO userLoginDTO) {
    return userRepository
        .findByUsernameAndIsActiveIsTrue(userLoginDTO.username())
        .filter(
            matchedUser ->
                UserValidationService.isPasswordMatched(userLoginDTO, matchedUser, passwordEncoder))
        .map(users -> accessTokenGenerator.generateAccessToken(users, UUID.randomUUID()))
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
}
