package com.vulinh.service.author;

import module java.base;

import com.vulinh.data.entity.Author;
import com.vulinh.data.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

  final AuthorRepository authorRepository;

  public Map<UUID, Author> findAuthorMapByIds(Collection<UUID> ids) {
    return authorRepository.findAllByIdIn(ids).stream()
        .collect(Collectors.toMap(Author::getId, author -> author));
  }
}
