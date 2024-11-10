package com.vulinh.data.repository;

import com.vulinh.data.entity.Comment;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends BaseRepository<Comment, UUID> {}
