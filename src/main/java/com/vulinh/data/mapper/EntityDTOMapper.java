package com.vulinh.data.mapper;

import com.vulinh.data.entity.AbstractIdentifiable;
import org.mapstruct.MappingTarget;

import java.io.Serializable;

public interface EntityDTOMapper<E extends AbstractIdentifiable<?>, D extends Serializable> {

  E toEntity(D dto);

  D toDto(E entity);

  // For update operations, depending on your business, you may have to override this method
  // And do not forget to add @MappingTarget annotation, as it did not support inheritance
  // (also known as @Inherited annotation)
  void merge(D dto, @MappingTarget E entity);
}
