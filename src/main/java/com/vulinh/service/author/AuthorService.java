package com.vulinh.service.author;

import module java.base;

import com.vulinh.data.entity.Author;
import com.vulinh.data.repository.AuthorRepository;
import com.vulinh.data.repository.PostRepository;
import com.vulinh.service.keycloak.KeycloakAdminClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

  final KeycloakAdminClientService keycloakAdminClientService;

  final AuthorRepository authorRepository;
  final PostRepository postRepository;

  final JwtDecoder jwtDecoder;

  // --- Startup initialization ---

  @Async
  @Transactional
  @EventListener(ContextRefreshedEvent.class)
  void initializeAuthors() {
    var missingAuthorIds = postRepository.findAuthorIdsNotInAuthorTable();

    if (missingAuthorIds.isEmpty()) {
      log.info("All post authors already exist in author table");
      return;
    }

    log.info("Found {} author IDs missing from author table", missingAuthorIds.size());

    for (var authorId : missingAuthorIds) {
      try {
        var keycloakUser = keycloakAdminClientService.getKeycloakUser(authorId);

        var displayName =
            Stream.of(keycloakUser.firstName(), keycloakUser.lastName())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(StringUtils.SPACE));

        saveAuthor(
            keycloakUser.id(),
            keycloakUser.username(),
            displayName.isBlank() ? keycloakUser.username() : displayName,
            keycloakUser.email());
      } catch (Exception e) {
        log.warn("Failed to fetch Keycloak user for author ID {}: {}", authorId, e.getMessage());
      }
    }

    log.info("Author initialization complete");
  }

  // --- Login-triggered population ---

  @Async
  @Transactional
  public void populateAuthorAsync(String accessToken) {
    try {
      var jwt = jwtDecoder.decode(accessToken);

      var userId = UUID.fromString(jwt.getSubject());

      if (authorRepository.existsById(userId)) {
        return;
      }

      var username = jwt.getClaimAsString("preferred_username");
      var email = jwt.getClaimAsString("email");
      var displayName = jwt.getClaimAsString("name");

      saveAuthor(
          userId, username, StringUtils.isBlank(displayName) ? username : displayName, email);
    } catch (Exception e) {
      log.warn("Failed to populate author from login: {}", e.getMessage());
    }
  }

  // --- Query helpers ---

  public Map<UUID, Author> findAuthorMapByIds(Collection<UUID> ids) {
    return authorRepository.findAllByIdIn(ids).stream()
        .collect(Collectors.toMap(Author::getId, author -> author));
  }

  // --- Shared helpers ---

  private void saveAuthor(UUID id, String username, String displayName, String email) {
    var author =
        Author.builder().id(id).username(username).displayName(displayName).email(email).build();

    authorRepository.save(author);

    log.info("Populated author: {} ({})", username, id);
  }
}
