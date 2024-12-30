package com.vulinh.data.repository;

import com.vulinh.constant.UserRole;
import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.Roles;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends BaseRepository<Roles, UserRole> {}
