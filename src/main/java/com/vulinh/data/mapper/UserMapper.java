package com.vulinh.data.mapper;

import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.dto.auth.UserRegistrationDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.dto.user.UserDTO;
import com.vulinh.data.entity.Users;
import java.util.stream.Collectors;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true), imports = Collectors.class)
public interface UserMapper extends EntityDTOMapper<Users, UserDTO> {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  UserDTO toUserDTO(Users users);

  UserBasicDTO toBasicUserDTO(Users users);

  @Override
  @Mapping(target = "userRegistrationCode", ignore = true)
  @Mapping(target = "passwordResetCode", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "username", expression = "java(dto.username().toLowerCase())")
  @Mapping(target = "email", expression = "java(dto.email().toLowerCase())")
  @Mapping(target = "passwordResetCodeExpiration", ignore = true)
  void merge(UserDTO dto, @MappingTarget Users entity);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "passwordResetCode", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "userRegistrationCode", source = "userRegistrationCode")
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "username", expression = "java(userRegistrationDTO.username().toLowerCase())")
  @Mapping(target = "email", expression = "java(userRegistrationDTO.email().toLowerCase())")
  @Mapping(target = "passwordResetCodeExpiration", ignore = true)
  Users toUserWithRegistrationCode(
      UserRegistrationDTO userRegistrationDTO, String userRegistrationCode);

  @Mapping(target = "userRoles", ignore = true)
  @Mapping(target = "username", expression = "java(userRegistrationDTO.username().toLowerCase())")
  @Mapping(target = "email", expression = "java(userRegistrationDTO.email().toLowerCase())")
  @Mapping(target = "userRegistrationCode", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "passwordResetCodeExpiration", ignore = true)
  @Mapping(target = "passwordResetCode", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  Users toUser(UserRegistrationDTO userRegistrationDTO);
}
