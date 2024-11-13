package com.vulinh.data.repository;

import com.vulinh.data.base.AbstractIdentifiable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractIdentifiable<I>, I>
    extends JpaRepository<E, I>, JpaSpecificationExecutor<E>, QuerydslPredicateExecutor<E> {}
