package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Users;
import com.vulinh.exception.InvalidCredentialsException;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<Users, UUID> {

  // username is unique
  Optional<Users> findByUsernameAndIsActiveIsTrue(String username);

  Optional<Users> findByIdAndIsActiveIsTrue(UUID id);

  boolean existsByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

  boolean existsByIdAndIsActiveIsTrue(UUID id);

  default Users findActiveUser(UUID id) {
    return findByIdAndIsActiveIsTrue(id)
        .orElseThrow(InvalidCredentialsException::invalidCredentialsException);
  }
}
