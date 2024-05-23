package com.vulinh.controller.impl;

import com.vulinh.controller.BcryptPublicAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.auth.PasswordRequestDTO;
import com.vulinh.data.dto.auth.PasswordResponseDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.service.user.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BcryptPublicController implements BcryptPublicAPI {

  private final PasswordEncoder passwordEncoder;

  @Override
  public GenericResponse<PasswordResponseDTO> generateEncodedPassword(
      PasswordRequestDTO passwordRequestDTO) {
    var rawPassword = passwordRequestDTO.rawPassword();

    if (StringUtils.isBlank(rawPassword)
        || rawPassword.length() < UserValidationService.PASSWORD_MINIMUM_LENGTH) {
      throw ExceptionBuilder.buildCommonException(
          "Invalid password", CommonMessage.MESSAGE_INVALID_PASSWORD);
    }

    var encodedPassword = passwordEncoder.encode(rawPassword);

    log.info("\nRaw password: {}\nEncoded password: {}", rawPassword, encodedPassword);

    return GenericResponse.success(
        PasswordResponseDTO.builder()
            .rawPassword(rawPassword)
            .encodedPassword(encodedPassword)
            .build());
  }
}
