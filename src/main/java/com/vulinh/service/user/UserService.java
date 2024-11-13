package com.vulinh.service.user;

import com.querydsl.core.types.Predicate;
import com.vulinh.constant.UserRole;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.locale.CommonMessage;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.dto.user.UserSearchDTO;
import com.vulinh.data.entity.*;
import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.RoleRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.service.BaseEntityService;
import com.vulinh.utils.QueryDSLPredicateBuilder;
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
public class UserService implements BaseEntityService<UUID, Users, UserDTO, UserRepository> {

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
  public EntityDTOMapper<Users, UserDTO> getMapper() {
    return USER_MAPPER;
  }

  @Transactional
  public UserDTO createUser(UserRegistrationDTO userRegistrationDTO) {
    userValidationService.validateUserCreation(userRegistrationDTO);

    var transientUser =
        USER_MAPPER.toUser(
            userRegistrationDTO.withPassword(
                passwordEncoder.encode(userRegistrationDTO.password())));

    var rawRoleNames = UserRole.fromRawRole(userRegistrationDTO.userRoles());

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

  public Page<UserDTO> search(UserSearchDTO userSearchDTO, Pageable pageable) {
    return findAll(buildSpecification(userSearchDTO), pageable);
  }

  private Predicate buildSpecification(UserSearchDTO userSearchDTO) {
    var identity = userSearchDTO.identity();
    var qUser = QUsers.users;

    var specification =
        QueryDSLPredicateBuilder.or(
            QueryDSLPredicateBuilder.likeIgnoreCase(qUser.id, identity),
            QueryDSLPredicateBuilder.likeIgnoreCase(qUser.username, identity),
            QueryDSLPredicateBuilder.likeIgnoreCase(qUser.email, identity),
            QueryDSLPredicateBuilder.likeIgnoreCase(qUser.fullName, identity));

    var searchRoles = UserRole.fromRawRole(userSearchDTO.roles());

    if (!searchRoles.isEmpty()) {
      var roles =
          roleRepository.findAllById(searchRoles).stream()
              .map(Roles::getId)
              .collect(Collectors.toSet());

      specification =
          QueryDSLPredicateBuilder.and(
              specification,
              roles.isEmpty()
                  ? QueryDSLPredicateBuilder.never()
                  : QUsers.users.userRoles.any().id.in(roles));
    }

    return specification;
  }
}
