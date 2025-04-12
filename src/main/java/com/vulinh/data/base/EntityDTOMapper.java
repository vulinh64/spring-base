package com.vulinh.data.base;

public interface EntityDTOMapper<E extends AbstractIdentifiable<?>, D> {

  E toEntity(D dto);

  D toDto(E entity);
}
