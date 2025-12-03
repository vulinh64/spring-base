package com.vulinh.service.keycloak;

import module java.base;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.data.dto.response.KeycloakUserResponse;
import com.vulinh.data.mapper.KeycloakMapper;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFound404Exception;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakAdminClientService {

  final ApplicationProperties applicationProperties;

  final Keycloak keycloak;

  @NonNull
  public KeycloakUserResponse getKeycloakUser(UUID userId) {
    try {
      return KeycloakMapper.INSTANCE.toKeycloakUserResponse(
          keycloak
              .realm(applicationProperties.security().realmName())
              .users()
              .get(String.valueOf(userId))
              .toRepresentation());
    } catch (NotFoundException notFoundException) {
      throw NotFound404Exception.invalidKeycloakUser(userId, notFoundException);
    } catch (ProcessingException processingException) {
      throw NoSuchPermissionException.noPermissionKeycloakClient(
          applicationProperties.keycloakAuthentication().username(), processingException);
    }
  }
}
