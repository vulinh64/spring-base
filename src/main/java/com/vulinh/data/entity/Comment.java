package com.vulinh.data.entity;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
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
public class Comment extends UuidJpaEntity implements DateTimeAuditable {

  @Serial private static final long serialVersionUID = 8024056047258352378L;

  @Id
  @UuidGenerator(style = Style.TIME)
  private UUID id;

  private String content;

  @CreatedDate private Instant createdDate;

  @LastModifiedDate private Instant updatedDate;

  // So that mapping on Post works
  @Column(name = "post_id")
  private UUID postId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private Users createdBy;
}
