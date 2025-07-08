package com.vulinh.service.user;

import com.querydsl.core.types.Predicate;
import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.request.UserSearchRequest;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.RoleRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.ResourceConflictException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.PageableQueryService;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.SecurityUtils;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService
    implements PageableQueryService<Users, SingleUserResponse, UserSearchRequest> {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserValidationService userValidationService;

  @Transactional
  public SingleUserResponse createUser(UserRegistrationRequest userRegistrationRequest) {
    userValidationService.validateUserCreation(userRegistrationRequest);

    var possibleUsername =
        userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(
            userRegistrationRequest.username(), userRegistrationRequest.email());

    if (possibleUsername.isPresent()) {
      var existedUser = possibleUsername.get();

      throw ResourceConflictException.resourceConflictException(
          "User [%s] (input username: [%s]) with email [%s] (input email: [%s]) already existed"
              .formatted(
                  existedUser.getUsername(),
                  userRegistrationRequest.username(),
                  existedUser.getEmail(),
                  userRegistrationRequest.email()),
          ServiceErrorCode.MESSAGE_USER_OR_EMAIL_EXISTED);
    }

    var transientUser =
        USER_MAPPER.toUser(
            userRegistrationRequest.withPassword(
                passwordEncoder.encode(userRegistrationRequest.password())));

    var rawRoleNames = UserRole.fromRawRole(userRegistrationRequest.userRoles());

    transientUser
        .setIsActive(true)
        .setUserRoles(
            Set.copyOf(
                roleRepository.findAllById(
                    rawRoleNames.isEmpty() ? Set.of(UserRole.USER) : rawRoleNames)));

    return USER_MAPPER.toDto(userRepository.save(transientUser));
  }

  @Transactional
  public boolean delete(UUID id) {
    SecurityUtils.getUserDTO()
        .filter(user -> user.id().equals(id))
        .ifPresent(
            ignored -> {
              throw NoSuchPermissionException.noSuchPermissionException(
                  "Cannot delete self", ServiceErrorCode.MESSAGE_NO_SELF_DESTRUCTION);
            });

    return userRepository
        .findById(id)
        .map(
            found -> {
              userRepository.delete(found);

              return true;
            })
        .isPresent();
  }

  /*
   * Pageable user query implementation
   */

  @Override
  @NonNull
  public SingleUserResponse toDto(@NonNull Users entity) {
    return USER_MAPPER.toDto(entity);
  }

  @Override
  public Predicate toPredicate(@NonNull UserSearchRequest searchCriteria) {
    var identity = searchCriteria.identity();
    var qUser = QUsers.users;

    var specification =
        PredicateBuilder.or(
            PredicateBuilder.likeIgnoreCase(qUser.id, identity),
            PredicateBuilder.likeIgnoreCase(qUser.username, identity),
            PredicateBuilder.likeIgnoreCase(qUser.email, identity),
            PredicateBuilder.likeIgnoreCase(qUser.fullName, identity));

    var searchRoles = UserRole.fromRawRole(searchCriteria.roles());

    if (!searchRoles.isEmpty()) {
      var roles =
          roleRepository.findAllById(searchRoles).stream()
              .map(Roles::getId)
              .collect(Collectors.toSet());

      specification =
          PredicateBuilder.and(
              specification,
              roles.isEmpty() ? PredicateBuilder.never() : qUser.userRoles.any().id.in(roles));
    }

    return specification;
  }

  @Override
  @NonNull
  public QuerydslPredicateExecutor<Users> getDslRepository() {
    return userRepository;
  }
}
