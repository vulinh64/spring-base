package com.vulinh.service.user;

import com.sanctionco.jmail.JMail;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.entity.Users;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
import com.vulinh.utils.validator.ValidatorStepImpl;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

  public static final int PASSWORD_MINIMUM_LENGTH = 8;
  public static final int USERNAME_MAX_LENGTH = 200;

  private final UserRepository userRepository;

  public static boolean isActive(Users matcherUser) {
    var result = BooleanUtils.isTrue(matcherUser.getIsActive());

    if (!result) {
      log.debug("User {} - {} is not active", matcherUser.getId(), matcherUser.getUsername());
    }

    return result;
  }

  public static boolean isPasswordMatched(
      UserLoginDTO userLoginDTO, Users matchedUser, PasswordEncoder passwordEncoder) {
    var result = passwordEncoder.matches(userLoginDTO.password(), matchedUser.getPassword());

    if (!result) {
      log.debug(
          "Invalid password for user {} - {}", matchedUser.getId(), matchedUser.getUsername());
    }

    return result;
  }

  public static boolean isRegistrationCodeMatched(String code, Users matchedUser) {
    var result = code.equals(matchedUser.getUserRegistrationCode());

    if (!result) {
      log.debug("Invalid registration code for user {}", matchedUser.getUsername());
    }

    return result;
  }

  public static boolean isEmailAvailable(UserRegistrationDTO dto) {
    var email = dto.email();

    var result = JMail.strictValidator().isValid(email);

    if (!result) {
      log.debug("Email {} is invalid", email);
    }

    return result;
  }

  public static ValidatorStep<UserRegistrationDTO> availableUsername(
      UserRepository userRepository) {
    return ValidatorStepImpl.of(
        dto -> {
          var username = dto.username();

          var result = !userRepository.existsByUsernameIgnoreCase(username);

          if (!result) {
            log.debug("User {} already existed", username);
          }

          return result;
        },
        CommonMessage.MESSAGE_USER_EXISTED,
        "User already existed");
  }

  public static ValidatorStep<UserRegistrationDTO> availableEmail(UserRepository userRepository) {
    return ValidatorStepImpl.of(
        dto -> {
          var email = dto.email();

          var result = !userRepository.existsByEmailIgnoreCase(email);

          if (!result) {
            log.debug("Email {} already existed", email);
          }

          return result;
        },
        CommonMessage.MESSAGE_EMAIL_EXISTED,
        "Email already existed");
  }

  public void validateUserCreation(UserRegistrationDTO userRegistrationDTO) {
    ValidatorChain.<UserRegistrationDTO>of()
        .addValidator(UserRule.values())
        .addValidator(availableUsername(userRepository), availableEmail(userRepository))
        .executeValidation(userRegistrationDTO);
  }

  @Getter
  @RequiredArgsConstructor
  public enum UserRule implements ValidatorStep<UserRegistrationDTO> {
    RULE_NO_BLANK_USERNAME(
        ValidatorStep.noBlankField(UserRegistrationDTO::username),
        CommonMessage.MESSAGE_INVALID_USERNAME,
        "Blank username is not allowed"),
    RULE_LONG_ENOUGH_USERNAME(
        ValidatorStep.noExceededLength(UserRegistrationDTO::username, USERNAME_MAX_LENGTH),
        CommonMessage.MESSAGE_INVALID_USERNAME,
        "Username is too long"),
    RULE_NO_BLANK_PASSWORD(
        ValidatorStep.noBlankField(UserRegistrationDTO::password),
        CommonMessage.MESSAGE_INVALID_PASSWORD,
        "Blank password is not allowed"),
    RULE_NO_BLANK_EMAIL(
        ValidatorStep.noBlankField(UserRegistrationDTO::email),
        CommonMessage.MESSAGE_INVALID_EMAIL,
        "Blank email is not allowed"),
    RULE_NO_INVALID_EMAIL(
        UserValidationService::isEmailAvailable,
        CommonMessage.MESSAGE_INVALID_EMAIL,
        "Wrong email format");

    private final Predicate<UserRegistrationDTO> predicate;
    private final CommonMessage errorMessage;
    private final String additionalMessage;
  }
}
