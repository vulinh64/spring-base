package com.vulinh.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.vulinh.data.dto.request.UserRegistrationRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class UserValidationServiceTest {

  @ParameterizedTest
  @ValueSource(strings = {"john_doe", "john.doe", "JohnDoe123", "john_doe.123"})
  void testValidUsername(String username) {
    assertTrue(UserValidationService.isUsernameValid(mockUserRegistrationRequest(username)));
  }

  @ParameterizedTest
  @CsvSource({
    // Invalid first character
    "_johndoe",
    ".johndoe",
    "1johndoe",
    // Invalid last character
    "johndoe_",
    "johndoe.",
    // Invalid user
    "john#doe",
    "john doe",
    "john$doe"
  })
  void testInvalidUsername(String username) {
    assertFalse(UserValidationService.isUsernameValid(mockUserRegistrationRequest(username)));
  }

  private static UserRegistrationRequest mockUserRegistrationRequest(String username) {
    return UserRegistrationRequest.builder().username(username).build();
  }
}
