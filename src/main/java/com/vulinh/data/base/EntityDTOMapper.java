package com.vulinh.data.base;

public interface EntityDTOMapper<E extends AbstractIdentifiable<?>, D> {

  D toDto(E entity);
}
