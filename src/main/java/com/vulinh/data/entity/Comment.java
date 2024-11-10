package com.vulinh.data.entity;

import com.vulinh.data.entity.AbstractIdentifiable.UUIDJpaEntity;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
public class Comment extends UUIDJpaEntity {

  @Serial private static final long serialVersionUID = 8024056047258352378L;

  @Id @UuidGenerator private UUID id;

  private String content;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;

  @CreatedBy private UUID createdBy;

  // So that mapping on Post works
  @Column(name = "post_id")
  private UUID postId;
}
