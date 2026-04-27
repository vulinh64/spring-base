package com.vulinh.service.author;

import module java.base;

import com.vulinh.data.entity.Author;
import com.vulinh.data.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

  final AuthorRepository authorRepository;

  final JwtDecoder jwtDecoder;

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

  public Map<UUID, Author> findAuthorMapByIds(Collection<UUID> ids) {
    return authorRepository.findAllByIdIn(ids).stream()
        .collect(Collectors.toMap(Author::getId, author -> author));
  }

  private void saveAuthor(UUID id, String username, String displayName, String email) {
    var author =
        Author.builder().id(id).username(username).displayName(displayName).email(email).build();

    authorRepository.save(author);

    log.info("Populated author: {} ({})", username, id);
  }
}
