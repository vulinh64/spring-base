package com.vulinh.data.entity;

import com.vulinh.data.base.AbstractIdentifiable;
import com.vulinh.data.entity.ids.CommentRevisionId;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.Instant;
import java.util.UUID;
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
public class CommentRevision extends AbstractIdentifiable<CommentRevisionId> {

  @Serial private static final long serialVersionUID = -159022567357111389L;

  @EmbeddedId private CommentRevisionId id;

  @Enumerated(EnumType.ORDINAL)
  private RevisionType revisionType;

  private String content;

  @CreatedDate private Instant revisionCreatedDate;

  @CreatedBy private UUID revisionCreatedBy;

  private UUID postId;
}
