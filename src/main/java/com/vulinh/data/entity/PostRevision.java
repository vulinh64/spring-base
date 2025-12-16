package com.vulinh.data.entity;

import module java.base;

import com.vulinh.data.base.AbstractEntity;
import com.vulinh.data.entity.ids.PostRevisionId;
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
public class PostRevision extends AbstractEntity<PostRevisionId> {

  @Serial private static final long serialVersionUID = -4374526557871349803L;

  @EmbeddedId PostRevisionId id;

  @Enumerated(EnumType.ORDINAL)
  RevisionType revisionType;

  String title;
  String slug;
  String excerpt;
  String postContent;
  UUID authorId;
  UUID categoryId;
  String tags;

  @CreatedDate Instant revisionCreatedDateTime;

  @CreatedBy UUID revisionCreatedBy;
}
