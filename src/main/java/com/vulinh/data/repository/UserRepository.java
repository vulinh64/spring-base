package com.vulinh.data.repository;

import com.vulinh.data.entity.Users;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<Users, String> {

  // username is unique
  Optional<Users> findByUsernameAndIsActiveIsTrue(String username);

  Optional<Users> findByIdAndIsActiveIsTrue(String id);

  boolean existsByUsernameIgnoreCase(String username);

  boolean existsByEmailIgnoreCase(String email);
}
