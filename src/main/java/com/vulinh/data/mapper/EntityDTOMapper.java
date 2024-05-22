package com.vulinh.data.mapper;

import com.vulinh.data.entity.AbstractIdentifiable;
import org.mapstruct.MappingTarget;

import java.io.Serializable;

public interface EntityDTOMapper<E extends AbstractIdentifiable<?>, D extends Serializable> {

  E toEntity(D dto);

  D toDto(E entity);

  void merge(D dto, @MappingTarget E entity);
}
