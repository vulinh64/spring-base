package com.vulinh.data.entity;

import module java.base;

import com.vulinh.data.base.AbstractTimestampAuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
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
public class Comment extends AbstractTimestampAuditableEntity<UUID> {

  @Serial private static final long serialVersionUID = 8024056047258352378L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  String content;

  // So that mapping on Post works
  @Column(name = "post_id")
  UUID postId;

  @CreatedBy UUID createdBy;

  @LastModifiedBy UUID updatedBy;
}
