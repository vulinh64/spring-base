package com.vulinh.service.user;

import com.sanctionco.jmail.JMail;
import com.vulinh.data.dto.auth.UserLoginDTO;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.entity.Users;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
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

  private static final ValidatorStepFactory VALIDATOR_STEP_FACTORY = ValidatorStepFactory.INSTANCE;

  public static final int PASSWORD_MINIMUM_LENGTH = 8;
  public static final int USERNAME_MAX_LENGTH = 200;

  private static final char UNDERSCORE = '_';

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

  public static boolean isUsernameValid(UserRegistrationDTO dto) {
    // Assuming the username is not blank
    var username = dto.username();

    var firstCharacter = username.charAt(0);

    if (firstCharacter == UNDERSCORE || Character.isDigit(firstCharacter)) {
      log.debug("Username {} with invalid first character ({})", username, firstCharacter);

      return false;
    }

    var lastCharacter = username.charAt(username.length() - 1);

    if (lastCharacter == UNDERSCORE) {
      log.debug("Username {} with invalid last character ({})", username, lastCharacter);

      return false;
    }

    var charArray = username.toCharArray();

    for (int index = 1; index < charArray.length - 1; index++) {
      var character = charArray[index];

      if (!(Character.isLetterOrDigit(character) || character == UNDERSCORE)) {
        log.debug(
            "Username {} with invalid character ({}) at position {}", username, character, index);

        return false;
      }
    }

    return true;
  }

  public static ValidatorStep<UserRegistrationDTO> availableUsername(
      UserRepository userRepository) {
    return VALIDATOR_STEP_FACTORY.build(
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
    return VALIDATOR_STEP_FACTORY.build(
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
    ValidatorChain.<UserRegistrationDTO>start()
        .addValidator(UserRule.values())
        .addValidator(availableUsername(userRepository), availableEmail(userRepository))
        .executeValidation(userRegistrationDTO);
  }

  @Getter
  @RequiredArgsConstructor
  public enum UserRule implements ValidatorStep<UserRegistrationDTO> {
    USER_NO_BLANK_USERNAME(
        ValidatorStepFactory.noBlankField(UserRegistrationDTO::username),
        CommonMessage.MESSAGE_INVALID_USERNAME,
        "Blank username is not allowed"),
    USER_LONG_ENOUGH_USERNAME(
        ValidatorStepFactory.noExceededLength(UserRegistrationDTO::username, USERNAME_MAX_LENGTH),
        CommonMessage.MESSAGE_INVALID_USERNAME,
        "Username exceeded %s characters".formatted(USERNAME_MAX_LENGTH)) {

      @Override
      public Object[] getArguments() {
        return new Integer[] {USERNAME_MAX_LENGTH};
      }
    },
    USER_VALID_USERNAME(
        UserValidationService::isUsernameValid,
        CommonMessage.MESSAGE_INVALID_USERNAME,
        """
        Invalid username (contains only alphanumeric characters and underscores, \
        first character cannot be a number or an underscore, and last character \
        cannot be an underscore)
        """),
    USER_NO_BLANK_PASSWORD(
        ValidatorStepFactory.noBlankField(UserRegistrationDTO::password),
        CommonMessage.MESSAGE_INVALID_PASSWORD,
        "Blank password is not allowed"),
    USER_PASSWORD_LONG(
        ValidatorStepFactory.atLeastLength(UserRegistrationDTO::password, PASSWORD_MINIMUM_LENGTH),
        CommonMessage.MESSAGE_INVALID_PASSWORD,
        "Password has to be %s characters or more".formatted(PASSWORD_MINIMUM_LENGTH)) {

      @Override
      public Object[] getArguments() {
        return new Integer[] {PASSWORD_MINIMUM_LENGTH};
      }
    },
    USER_NO_BLANK_EMAIL(
        ValidatorStepFactory.noBlankField(UserRegistrationDTO::email),
        CommonMessage.MESSAGE_INVALID_EMAIL,
        "Blank email is not allowed"),
    USER_NO_INVALID_EMAIL(
        UserValidationService::isEmailAvailable,
        CommonMessage.MESSAGE_INVALID_EMAIL,
        "Wrong email format");

    private final Predicate<UserRegistrationDTO> predicate;
    private final CommonMessage errorMessage;
    private final String additionalMessage;
    private final Object[] arguments = {};
  }
}
