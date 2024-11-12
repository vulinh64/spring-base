package com.vulinh.data.entity;

import com.vulinh.data.DateTimeAuditable;
import com.vulinh.data.entity.AbstractIdentifiable.UUIDJpaEntity;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
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
public class Post extends UUIDJpaEntity implements DateTimeAuditable {

  @Serial private static final long serialVersionUID = -2260038525808618984L;

  @Id @UuidGenerator private UUID id;

  private String title;
  private String slug;
  private String excerpt;
  private String postContent;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;

  @LastModifiedBy private UUID updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Users author;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Category category;

  @OneToMany
  @JoinTable(
      name = "post_tag_mapping",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @ToString.Exclude
  @OrderBy("displayName asc")
  private Set<Tag> tags;

  // This generates many delete statement, fixing...
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id", updatable = false, insertable = false)
  @OrderBy("createdDate desc")
  @ToString.Exclude
  private List<Comment> comments;
}
