package com.vulinh.data.mapper;

import com.vulinh.data.entity.AbstractIdentifiable;
import java.io.Serializable;
import org.mapstruct.MappingTarget;

public interface EntityDTOMapper<E extends AbstractIdentifiable, D extends Serializable> {

  E toEntity(D dto);

  D toDto(E entity);

  void merge(D dto, @MappingTarget E entity);
}
