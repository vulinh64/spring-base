package com.vulinh.data.mapper;

import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.dto.request.UserRegistrationRequest;
import com.vulinh.data.dto.response.SingleUserResponse;
import com.vulinh.data.dto.response.UserBasicResponse;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.Users;
import java.util.stream.Collectors;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
    builder = @Builder(disableBuilder = true),
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = Collectors.class)
public interface UserMapper extends EntityDTOMapper<Users, SingleUserResponse>, DateTimeMappable {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "userRoles", ignore = true)
  SingleUserResponse toUserDTO(Users users);

  @Mapping(target = "sessionId", source = "userSession.id.sessionId")
  @Mapping(target = "id", source = "user.id")
  @Mapping(target = "createdDate", source = "user.createdDate")
  @Mapping(target = "updatedDate", source = "user.updatedDate")
  UserBasicResponse toBasicUserDTO(Users user, UserSession userSession);

  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "passwordResetCode", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "userRegistrationCode", source = "userRegistrationCode")
  @Mapping(target = "userRoles", ignore = true)
  @Mapping(
      target = "username",
      expression = "java(userRegistrationRequest.username().toLowerCase())")
  @Mapping(target = "email", expression = "java(userRegistrationRequest.email().toLowerCase())")
  @Mapping(target = "passwordResetCodeExpiration", ignore = true)
  Users toUserWithRegistrationCode(
      UserRegistrationRequest userRegistrationRequest, String userRegistrationCode);

  @Mapping(target = "userRoles", ignore = true)
  @Mapping(
      target = "username",
      expression = "java(userRegistrationRequest.username().toLowerCase())")
  @Mapping(target = "email", expression = "java(userRegistrationRequest.email().toLowerCase())")
  @Mapping(target = "userRegistrationCode", ignore = true)
  @Mapping(target = "updatedDate", ignore = true)
  @Mapping(target = "passwordResetCodeExpiration", ignore = true)
  @Mapping(target = "passwordResetCode", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  Users toUser(UserRegistrationRequest userRegistrationRequest);
}
