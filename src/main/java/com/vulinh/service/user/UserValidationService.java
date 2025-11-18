package com.vulinh.service.user;

import module java.base;

import com.sanctionco.jmail.JMail;
import com.vulinh.data.base.ApplicationError;
import com.vulinh.data.dto.request.UserLoginRequest;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.entity.Users;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import com.vulinh.utils.validator.ValidatorChain;
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

  final UserRepository userRepository;

  public static boolean isActive(Users matcherUser) {
    var result = BooleanUtils.isTrue(matcherUser.getIsActive());

    log.debug(
        "User {} - {} is {}",
        matcherUser.getId(),
        matcherUser.getUsername(),
        result ? "active" : "not active");

    return result;
  }

  public static boolean isPasswordMatched(
      UserLoginRequest userLoginRequest, Users matchedUser, PasswordEncoder passwordEncoder) {
    var result = passwordEncoder.matches(userLoginRequest.password(), matchedUser.getPassword());

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

  public static boolean isEmailAvailable(UserRegistrationRequest dto) {
    var email = dto.email();

    var result = JMail.strictValidator().isValid(email);

    if (!result) {
      log.debug("Email {} is invalid", email);
    }

    return result;
  }

  public static boolean isUsernameValid(UserRegistrationRequest dto) {
    // Assuming the username is not blank
    var username = dto.username();

    var firstCharacter = username.charAt(0);

    if (Character.isDigit(firstCharacter) || isUnderscoreOrDot(firstCharacter)) {
      log.debug("Username {} with invalid first character ({})", username, firstCharacter);

      return false;
    }

    var lastCharacter = username.charAt(username.length() - 1);

    if (isUnderscoreOrDot(lastCharacter)) {
      log.debug("Username {} with invalid last character ({})", username, lastCharacter);

      return false;
    }

    var charArray = username.toCharArray();

    // First and last characters are already checked
    for (int index = 1; index < charArray.length - 1; index++) {
      var character = charArray[index];

      if (!(Character.isLetterOrDigit(character) || isUnderscoreOrDot(character))) {
        log.debug(
            "Username {} with invalid character ({}) at position {}", username, character, index);

        return false;
      }
    }

    return true;
  }

  public void validateUserCreation(UserRegistrationRequest userRegistrationRequest) {
    ValidatorChain.<UserRegistrationRequest>start()
        .addValidator(UserRule.values())
        .addValidator(userAvailabilityValidation(userRegistrationRequest))
        .executeValidation(userRegistrationRequest);
  }

  NoArgsValidatorStep<UserRegistrationRequest> userAvailabilityValidation(
      UserRegistrationRequest userRegistrationRequest) {
    return new NoArgsValidatorStep<>() {

      @Override
      public Predicate<UserRegistrationRequest> getPredicate() {
        return Predicate.not(
            _ ->
                userRepository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(
                    userRegistrationRequest.username(), userRegistrationRequest.email()));
      }

      @Override
      public ApplicationError getApplicationError() {
        return ServiceErrorCode.MESSAGE_USER_OR_EMAIL_EXISTED;
      }

      @Override
      public String getExceptionMessage() {
        return "Username [%s] or email [%s] already existed!"
            .formatted(userRegistrationRequest.username(), userRegistrationRequest.email());
      }
    };
  }

  static boolean isUnderscoreOrDot(char character) {
    return character == '_' || character == '.';
  }

  @Getter
  @RequiredArgsConstructor
  public enum UserRule implements NoArgsValidatorStep<UserRegistrationRequest> {
    USER_NO_BLANK_USERNAME(
        ValidatorStepFactory.noBlankField(UserRegistrationRequest::username),
        ServiceErrorCode.MESSAGE_INVALID_USERNAME,
        "Blank username is not allowed"),
    USER_LONG_ENOUGH_USERNAME(
        ValidatorStepFactory.noExceededLength(
            UserRegistrationRequest::username, USERNAME_MAX_LENGTH),
        ServiceErrorCode.MESSAGE_INVALID_USERNAME,
        "Username exceeded %s characters".formatted(USERNAME_MAX_LENGTH)) {

      @Override
      public Object[] getArgs() {
        return new Integer[] {USERNAME_MAX_LENGTH};
      }
    },
    USER_VALID_USERNAME(
        UserValidationService::isUsernameValid,
        ServiceErrorCode.MESSAGE_INVALID_USERNAME,
        "Username must contain only letters, digits, dot, and underscore; must not start with a digit, underscore, or dot; and must not end with an underscore or dot."),
    USER_NO_BLANK_PASSWORD(
        ValidatorStepFactory.noBlankField(UserRegistrationRequest::password),
        ServiceErrorCode.MESSAGE_INVALID_PASSWORD,
        "Blank password is not allowed"),
    USER_PASSWORD_LONG(
        ValidatorStepFactory.atLeastLength(
            UserRegistrationRequest::password, PASSWORD_MINIMUM_LENGTH),
        ServiceErrorCode.MESSAGE_INVALID_PASSWORD,
        "Password has to be %s characters or more".formatted(PASSWORD_MINIMUM_LENGTH)) {

      @Override
      public Object[] getArgs() {
        return new Integer[] {PASSWORD_MINIMUM_LENGTH};
      }
    },
    USER_NO_BLANK_EMAIL(
        ValidatorStepFactory.noBlankField(UserRegistrationRequest::email),
        ServiceErrorCode.MESSAGE_INVALID_EMAIL,
        "Blank email is not allowed"),
    USER_NO_INVALID_EMAIL(
        UserValidationService::isEmailAvailable,
        ServiceErrorCode.MESSAGE_INVALID_EMAIL,
        "Wrong email format");

    final Predicate<UserRegistrationRequest> predicate;
    final ServiceErrorCode applicationError;
    final String exceptionMessage;
  }
}
