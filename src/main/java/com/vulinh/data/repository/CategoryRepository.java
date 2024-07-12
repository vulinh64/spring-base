package com.vulinh.data.repository;

import com.vulinh.data.entity.Category;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends BaseRepository<Category, UUID> {}
