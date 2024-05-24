package com.vulinh.data.entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
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
public class PostRevision extends AbstractIdentifiable {

  @Serial private static final long serialVersionUID = -4374526557871349803L;

  @EmbeddedId private PostRevisionId id;

  @Enumerated(EnumType.ORDINAL)
  private RevisionType revisionType;

  private String title;
  private String slug;
  private String excerpt;
  private String postContent;
  private String authorId;
  private String categoryId;
  private String tags;

  @CreatedDate private LocalDateTime revisionCreatedDate;

  @CreatedBy String revisionCreatedBy;
}
