package com.vulinh.service.auth;

import com.vulinh.data.dto.request.PasswordChangeRequest;
import com.vulinh.data.entity.Users;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.service.user.UserValidationService;
import com.vulinh.utils.validator.NoArgsValidatorStep;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

  private static final int PASSWORD_MIN_LENGTH = 8;

  private final PasswordEncoder passwordEncoder;

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
        CommonMessage.MESSAGE_INVALID_PASSWORD,
        "Blank old password is not allowed"),
    RULE_NO_BLANK_NEW_PASSWORD(
        ValidatorStepFactory.noBlankField(PasswordChangeRequest::newPassword),
        CommonMessage.MESSAGE_INVALID_NEW_PASSWORD,
        "Blank new password is not allowed"),
    RULE_LONG_ENOUGH_NEW_PASSWORD(
        ValidatorStepFactory.atLeastLength(
            PasswordChangeRequest::newPassword, UserValidationService.PASSWORD_MINIMUM_LENGTH),
        CommonMessage.MESSAGE_INVALID_NEW_PASSWORD,
        "New password must have more than %s characters".formatted(PASSWORD_MIN_LENGTH)) {

      @Override
      public Integer[] getArguments() {
        return new Integer[] {PASSWORD_MIN_LENGTH};
      }
    };

    private final Predicate<PasswordChangeRequest> predicate;
    private final CommonMessage error;
    private final String exceptionMessage;
  }
}
