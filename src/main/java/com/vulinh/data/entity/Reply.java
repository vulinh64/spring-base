package com.vulinh.data.entity;

import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
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
public class Reply extends UuidJpaEntity {

  @Serial private static final long serialVersionUID = 5015961625466302323L;

  @Id
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  private UUID id;

  private String content;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private Users createdBy;
}
