package com.vulinh.data.repository;

import com.vulinh.data.base.BaseRepository;
import com.vulinh.data.entity.UserSession;
import com.vulinh.data.entity.ids.UserSessionId;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends BaseRepository<UserSession, UserSessionId> {}
