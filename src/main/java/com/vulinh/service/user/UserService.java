package com.vulinh.service.user;

import static com.vulinh.constant.UserRole.DEFAULT_ROLE;

import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.dto.user.UserSearchDTO;
import com.vulinh.data.entity.*;
import com.vulinh.data.mapper.EntityDTOMapper;
import com.vulinh.data.mapper.UserMapper;
import com.vulinh.data.repository.RoleRepository;
import com.vulinh.data.repository.UserRepository;
import com.vulinh.exception.CommonException;
import com.vulinh.service.BaseEntityService;
import com.vulinh.utils.SecurityUtils;
import com.vulinh.utils.SpecificationBuilder;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class UserService implements BaseEntityService<String, Users, UserDTO, UserRepository> {

  private static final UserMapper USER_MAPPER = UserMapper.INSTANCE;

  // This name MUST BE repository, so that Lombok will generate a getRepository() method
  // And this will help us implement the getRepository() from BaseEntityService interface
  // If you choose other name, like userRepository, then remember to return this when
  // implementing your own getRepository() method
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

    var rawRoleNames = userRegistrationDTO.userRoles();

    transientUser
        .setIsActive(true)
        .setUserRoles(
            roleRepository.findAllById(rawRoleNames.isEmpty() ? DEFAULT_ROLE : rawRoleNames));

    return USER_MAPPER.toDto(repository.save(transientUser));
  }

  @Transactional
  public Optional<UserBasicDTO> findByIdAndIsActiveIsTrue(String id) {
    return repository.findByIdAndIsActiveIsTrue(id).map(USER_MAPPER::toBasicUserDTO);
  }

  @Transactional
  public boolean delete(String id, HttpServletRequest httpServletRequest) {
    SecurityUtils.getUserDTO(httpServletRequest)
        .filter(user -> user.id().equals(id))
        .ifPresent(
            ignored -> {
              throw new CommonException(
                  "Cannot delete self", CommonMessage.MESSAGE_NO_SELF_DESTRUCTION);
            });

    return BaseEntityService.super.delete(id);
  }

  public Page<UserDTO> search(UserSearchDTO userSearchDTO, Pageable pageable) {
    return findAll(buildSpecification(userSearchDTO), pageable);
  }

  private Specification<Users> buildSpecification(UserSearchDTO userSearchDTO) {
    var identity = userSearchDTO.identity();

    var specification =
        SpecificationBuilder.or(
            SpecificationBuilder.like(Users_.id, identity),
            SpecificationBuilder.like(Users_.username, identity),
            SpecificationBuilder.like(Users_.email, identity),
            SpecificationBuilder.like(Users_.fullName, identity));

    var searchRoles = userSearchDTO.roles();

    if (!searchRoles.isEmpty()) {
      var roles =
          roleRepository.findAllById(searchRoles).stream()
              .map(Roles::getId)
              .collect(Collectors.toSet());

      specification =
          SpecificationBuilder.and(
              specification,
              roles.isEmpty()
                  ? SpecificationBuilder.never()
                  : (root, query, criteriaBuilder) ->
                      root.join(Users_.userRoles).get(Roles_.id).in(roles));
    }

    return specification;
  }
}
