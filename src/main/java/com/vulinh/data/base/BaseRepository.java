package com.vulinh.data.base;

import module java.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractIdentifiable<I>, I extends Serializable>
    extends JpaRepository<E, I>,
        JpaSpecificationExecutor<E>,
        ListQuerydslPredicateExecutor<E>,
        QueryByExampleExecutor<E> {}
