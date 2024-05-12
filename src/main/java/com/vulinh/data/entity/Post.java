package com.vulinh.data.entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
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
@With
public class Post extends AbstractIdentifiable.StringAbstractIdentifiable {

  @Serial private static final long serialVersionUID = -2260038525808618984L;

  @Id @UuidGenerator private String id;

  private String title;
  private String slug;
  private String excerpt;
  private String postContent;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;

  @LastModifiedBy private String updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Users author;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Category category;

  @ManyToMany
  @JoinTable(
      name = "post_tag_mapping",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @ToString.Exclude
  private Collection<Tag> tags;
}
