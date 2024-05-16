package com.vulinh.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E, I>
    extends JpaRepository<E, I>, JpaSpecificationExecutor<E>, QuerydslPredicateExecutor<E> {}
