package com.vulinh.service.user;

import com.querydsl.core.types.Predicate;
import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.constant.UserRole;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.request.UserSearchRequest;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.RoleRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.service.BaseEntityService;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@RequiredArgsConstructor
public class UserService
    implements BaseEntityService<UUID, Users, SingleUserResponse, UserRepository> {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  /*
   * For Lombok to generate the getRepository() method, the field name must be repository.
   * This method fulfills the getRepository() requirement of the BaseEntityService interface.
   * If you use a different name, like userRepository, ensure you return that instance in
   * your custom getRepository() implementation.
   */
  private final UserRepository repository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserValidationService userValidationService;

  @Override
  public EntityDTOMapper<Users, SingleUserResponse> getMapper() {
    return USER_MAPPER;
  }

  @Transactional
  public SingleUserResponse createUser(UserRegistrationRequest userRegistrationRequest) {
    userValidationService.validateUserCreation(userRegistrationRequest);

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

    return USER_MAPPER.toDto(repository.save(transientUser));
  }

  @Transactional
  public boolean delete(UUID id, HttpServletRequest httpServletRequest) {
    SecurityUtils.getUserDTO(httpServletRequest)
        .filter(user -> user.id().equals(id))
        .ifPresent(
            ignored -> {
              throw ExceptionFactory.INSTANCE.buildCommonException(
                  "Cannot delete self", CommonMessage.MESSAGE_NO_SELF_DESTRUCTION);
            });

    return BaseEntityService.super.delete(id);
  }

  public Page<SingleUserResponse> search(UserSearchRequest userSearchRequest, Pageable pageable) {
    return findAll(buildSpecification(userSearchRequest), pageable);
  }

  private Predicate buildSpecification(UserSearchRequest userSearchRequest) {
    var identity = userSearchRequest.identity();
    var qUser = QUsers.users;

    var specification =
        PredicateBuilder.or(
            PredicateBuilder.likeIgnoreCase(qUser.id, identity),
            PredicateBuilder.likeIgnoreCase(qUser.username, identity),
            PredicateBuilder.likeIgnoreCase(qUser.email, identity),
            PredicateBuilder.likeIgnoreCase(qUser.fullName, identity));

    var searchRoles = UserRole.fromRawRole(userSearchRequest.roles());

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
}
