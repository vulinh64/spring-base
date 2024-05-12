package com.vulinh.service.auth;

import com.vulinh.data.dto.auth.PasswordChangeDTO;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.event.UserRegistrationEventDTO;
import com.vulinh.data.dto.security.AccessToken;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.auth.PasswordValidationService.PasswordChangeRule;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.security.JwtGenerationUtils;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStepImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtGenerationUtils jwtGenerationUtils;

  private final UserValidationService userValidationService;
  private final PasswordValidationService passwordValidationService;

  private final ApplicationEventPublisher applicationEventPublisher;

  public AccessToken login(UserLoginDTO userLoginDTO) {
    return userRepository
        .findByUsername(userLoginDTO.username())
        .filter(
            matchedUser ->
                UserValidationService.isPasswordMatched(userLoginDTO, matchedUser, passwordEncoder))
        .filter(UserValidationService::isActive)
        .map(jwtGenerationUtils::generateAccessToken)
        .orElseThrow(ExceptionBuilder::invalidCredentials);
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

    applicationEventPublisher.publishEvent(UserRegistrationEventDTO.of(newUser));

    return USER_MAPPER.toUserDTO(newUser);
  }

  // Confirm normal user
  @Transactional
  public boolean confirmUser(String userId, String code) {
    if (StringUtils.isBlank(userId) || StringUtils.isBlank(code)) {
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
    ValidatorChain.<PasswordChangeDTO>of()
        .addValidator(PasswordChangeRule.values())
        .executeValidation(passwordChangeDTO);

    var userEntity =
        SecurityUtils.getUserDTO(httpServletRequest)
            .map(UserBasicDTO::id)
            .flatMap(userRepository::findByIdAndIsActiveIsTrue)
            .orElseThrow(ExceptionBuilder::invalidAuthorization);

    ValidatorChain.<PasswordChangeDTO>of()
        .addValidator(
            ValidatorStepImpl.of(
                passwordValidationService.isOldPasswordMatched(userEntity),
                CommonMessage.MESSAGE_PASSWORD_MISMATCHED,
                "Invalid old password"),
            ValidatorStepImpl.of(
                passwordValidationService.isNewPasswordNotMatched(userEntity),
                CommonMessage.MESSAGE_SAME_OLD_PASSWORD,
                "New password cannot be the same as old password"))
        .executeValidation(passwordChangeDTO);

    userRepository.save(
        userEntity.setPassword(passwordEncoder.encode(passwordChangeDTO.newPassword())));
  }
}
