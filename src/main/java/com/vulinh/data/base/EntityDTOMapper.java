package com.vulinh.data.base;

import org.mapstruct.MappingTarget;

public interface EntityDTOMapper<E extends AbstractIdentifiable<?>, D> {

  E toEntity(D dto);

  D toDto(E entity);

  void merge(D dto, @MappingTarget E entity);
}
