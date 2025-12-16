package com.vulinh.data.base;

public interface EntityDTOMapper<E extends AbstractEntity<?>, D> {

  D toDto(E entity);
}
