package com.vulinh.data.entity;

import module java.base;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.*;
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
  UUID id;

  String content;

  @CreatedDate Instant createdDate;

  @LastModifiedDate Instant updatedDate;

  // So that mapping on Post works
  @Column(name = "post_id")
  UUID postId;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  Users createdBy;
}
