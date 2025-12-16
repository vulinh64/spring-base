package com.vulinh.data.entity;

import module java.base;

import com.vulinh.data.base.AbstractEntity;
import com.vulinh.data.entity.ids.CommentRevisionId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
public class CommentRevision extends AbstractEntity<CommentRevisionId> {

  @Serial private static final long serialVersionUID = -159022567357111389L;

  @EmbeddedId CommentRevisionId id;

  @Enumerated(EnumType.ORDINAL)
  RevisionType revisionType;

  String content;

  @CreatedDate Instant revisionCreatedDateTime;

  @CreatedBy UUID revisionCreatedBy;

  UUID postId;
}
