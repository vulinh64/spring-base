package com.vulinh.service.keycloak;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFound404Exception;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakClientService {

  private final ApplicationProperties applicationProperties;

  final Keycloak keycloak;

  public UserRepresentation getUserRepresentation(UUID userId) {
    try {
      return keycloak
          .realm(applicationProperties.security().realmName())
          .users()
          .get(String.valueOf(userId))
          .toRepresentation();
    } catch (NotFoundException notFoundException) {
      throw NotFound404Exception.invalidKeycloakUser(userId, notFoundException);
    } catch (ProcessingException processingException) {
      throw NoSuchPermissionException.noPermissionKeycloakClient(
          applicationProperties.keycloakAuthentication().username(), processingException);
    }
  }
}
