package com.vulinh.it;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.querydsl.core.types.Predicate;
import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.constant.EndpointConstant.AuthEndpoint;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.entity.QUsers;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.utils.JsonUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestMethodOrder(OrderAnnotation.class)
class UserRegistrationFlowIT extends IntegrationTestBase {

  static final String TEST_USERNAME = "linh.nguyen";

  @Autowired UserRepository userRepository;

  @Order(0)
  @Test
  @Transactional
  @Commit
  @SneakyThrows
  void testRegisterUser() {
    var registerUserResult =
        getMockMvc()
            .perform(
                postWithEndpointAndPayload(
                    EndpointConstant.ENDPOINT_AUTH + AuthEndpoint.REGISTER,
                    UserRegistrationRequest.builder()
                        .username(TEST_USERNAME)
                        .password("12345678")
                        .fullName("Linh Nguyen")
                        .email("linh.nguyen@email.com")
                        .build()))
            .andExpect(status().isCreated())
            .andReturn();

    assertNotNull(registerUserResult);

    var response = registerUserResult.getResponse();

    assertNotNull(response);

    var userRegistrationResponse =
        JsonUtils.toObject(
                response.getContentAsString(),
                new TypeReference<GenericResponse<SingleUserResponse>>() {})
            .data();

    var userId = userRegistrationResponse.id();

    userRepository
        .findById(userId)
        .ifPresentOrElse(
            user -> {
              assertNotNull(user.getUserRegistrationCode());
              assertFalse(user.getIsActive());
            },
            () -> fail("User not found"));
  }

  @Order(1)
  @Test
  @Transactional
  @Commit
  void testConfirmUser() {
    userRepository
        .findOne(byUserName())
        .ifPresentOrElse(
            user -> {
              try {
                getMockMvc()
                    .perform(
                        get(EndpointConstant.ENDPOINT_AUTH + AuthEndpoint.CONFIRM_USER)
                            .param("userId", String.valueOf(user.getId()))
                            .param("code", user.getUserRegistrationCode()))
                    .andExpect(status().isOk());
              } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
              }
            },
            () -> fail("User not found"));
  }

  @Order(2)
  @Test
  @Transactional(readOnly = true)
  void testVerifyConfirmedUser() {
    userRepository
        .findOne(byUserName())
        .ifPresentOrElse(
            user -> {
              assertNull(user.getUserRegistrationCode());
              assertTrue(user.getIsActive());
            },
            () -> fail("User not found"));
  }

  @DynamicPropertySource
  static void reinitializeConnectionProperties(DynamicPropertyRegistry registry) {
    reinitializeConnectionPropertiesInternal(registry);
  }

  private static Predicate byUserName() {
    return QUsers.users.username.eq(TEST_USERNAME);
  }
}
