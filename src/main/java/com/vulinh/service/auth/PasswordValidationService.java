package com.vulinh.service.auth;

import module java.base;

import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.entity.Users;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

  static final int PASSWORD_MIN_LENGTH = 8;

  final PasswordEncoder passwordEncoder;

  public Predicate<PasswordChangeRequest> isOldPasswordMatched(Users user) {
    return dto -> passwordEncoder.matches(dto.oldPassword(), user.getPassword());
  }

  public Predicate<PasswordChangeRequest> isNewPasswordNotMatched(Users user) {
    return Predicate.not(dto -> passwordEncoder.matches(dto.newPassword(), user.getPassword()));
  }

  @RequiredArgsConstructor
  @Getter
  public enum PasswordChangeRule implements NoArgsValidatorStep<PasswordChangeRequest> {
    RULE_NO_BLANK_PASSWORD(
        ValidatorStepFactory.noBlankField(PasswordChangeRequest::oldPassword),
        ServiceErrorCode.MESSAGE_INVALID_PASSWORD,
        "Blank old password is not allowed"),
    RULE_NO_BLANK_NEW_PASSWORD(
        ValidatorStepFactory.noBlankField(PasswordChangeRequest::newPassword),
        ServiceErrorCode.MESSAGE_INVALID_NEW_PASSWORD,
        "Blank new password is not allowed"),
    RULE_LONG_ENOUGH_NEW_PASSWORD(
        ValidatorStepFactory.atLeastLength(
            PasswordChangeRequest::newPassword, UserValidationService.PASSWORD_MINIMUM_LENGTH),
        ServiceErrorCode.MESSAGE_INVALID_NEW_PASSWORD,
        "New password must have more than %s characters".formatted(PASSWORD_MIN_LENGTH)) {

      @Override
      public Integer[] getArgs() {
        return new Integer[] {PASSWORD_MIN_LENGTH};
      }
    };

    final Predicate<PasswordChangeRequest> predicate;
    final ServiceErrorCode applicationError;
    final String exceptionMessage;
  }
}
