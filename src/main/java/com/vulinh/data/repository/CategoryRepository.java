package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends BaseRepository<Category, UUID> {}
