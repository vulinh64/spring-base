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
public class Post extends AbstractTimestampAuditableEntity<UUID> {

  @Serial private static final long serialVersionUID = -2260038525808618984L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  String title;
  String slug;
  String excerpt;
  String postContent;

  @CreatedBy UUID authorId;
  @LastModifiedBy UUID updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  Category category;

  @OneToMany
  @JoinTable(
      name = "post_tag_mapping",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @ToString.Exclude
  @OrderBy("displayName asc")
  Set<Tag> tags;

  // This generates many delete statement, fixing...
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "post_id", updatable = false, insertable = false)
  @OrderBy("createdDateTime desc")
  @ToString.Exclude
  List<Comment> comments;
}
