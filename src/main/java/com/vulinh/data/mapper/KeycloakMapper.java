package com.vulinh.data.mapper;

import com.vulinh.data.dto.response.KeycloakUserResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = BooleanUtils.class)
public interface KeycloakMapper {

  KeycloakMapper INSTANCE = Mappers.getMapper(KeycloakMapper.class);

  @Mapping(target = "isEnabled", expression = "java(BooleanUtils.isTrue(user.isEnabled()))")
  KeycloakUserResponse toKeycloakUserResponse(UserRepresentation user);
}
