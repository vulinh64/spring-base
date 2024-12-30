package com.vulinh.service;

import com.querydsl.core.types.Predicate;
import com.vulinh.data.base.AbstractIdentifiable;
import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.base.BaseRepository;
import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseEntityService<
    I extends Serializable,
    E extends AbstractIdentifiable<I>,
    D extends Serializable,
    R extends BaseRepository<E, I>> {

  R getRepository();

  EntityDTOMapper<E, D> getMapper();

  // START: Basic CRUD operations

  default D save(D dto) {
    var mapper = getMapper();

    var entity = mapper.toEntity(dto);

    return mapper.toDto(getRepository().save(entity));
  }

  default Page<D> findAll(Pageable pageable) {
    return getRepository().findAll(pageable).map(entity -> getMapper().toDto(entity));
  }

  default Page<D> findAll(Specification<E> specification, Pageable pageable) {
    return getRepository()
        .findAll(specification, pageable)
        .map(entity -> getMapper().toDto(entity));
  }

  default Page<D> findAll(Predicate predicate, Pageable pageable) {
    return getRepository().findAll(predicate, pageable).map(entity -> getMapper().toDto(entity));
  }

  default boolean delete(I id) {
    var repository = getRepository();

    return repository
        .findById(id)
        .map(
            found -> {
              repository.delete(found);

              return true;
            })
        .isPresent();
  }

  default boolean delete(Iterable<I> ids) {
    var matchedByIds = getRepository().findAllById(ids);

    return deleteInternal(matchedByIds);
  }

  default boolean delete(Specification<E> specification) {
    var matchedByIds = getRepository().findAll(specification);

    return deleteInternal(matchedByIds);
  }

  default boolean delete(Predicate predicate) {
    var matchedByIds = getRepository().findAll(predicate);

    return deleteInternal(matchedByIds);
  }

  private boolean deleteInternal(Iterable<? extends E> matchedByIds) {
    getRepository().deleteAll(matchedByIds);

    return true;
  }

  // END: Basic CRUD operations
}
