package com.vulinh.data.repository;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.exception.CommonException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E, I>
    extends JpaRepository<E, I>, JpaSpecificationExecutor<E>, QuerydslPredicateExecutor<E> {

  default E findByIdOrFailed(I id, String entityName) {
    return findById(id)
        .orElseThrow(
            () ->
                new CommonException(
                    "Entity %s with ID %s not found".formatted(entityName, id),
                    CommonMessage.MESSAGE_INVALID_ENTITY_ID, null, entityName));
  }
}
