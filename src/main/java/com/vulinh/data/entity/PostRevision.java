package com.vulinh.data.entity;

import com.vulinh.data.base.AbstractIdentifiable;
import com.vulinh.data.entity.ids.PostRevisionId;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
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
public class PostRevision extends AbstractIdentifiable<PostRevisionId> {

  @Serial private static final long serialVersionUID = -4374526557871349803L;

  @EmbeddedId private PostRevisionId id;

  @Enumerated(EnumType.ORDINAL)
  private RevisionType revisionType;

  private String title;
  private String slug;
  private String excerpt;
  private String postContent;
  private UUID authorId;
  private UUID categoryId;
  private String tags;

  @CreatedDate private LocalDateTime revisionCreatedDate;

  @CreatedBy UUID revisionCreatedBy;
}
