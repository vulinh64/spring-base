package com.vulinh.data.entity;

import module java.base;

import com.vulinh.annotation.UUIDAsIdIfNullGenerator;
import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Category extends UuidJpaEntity {

  @Serial private static final long serialVersionUID = 106688923162808538L;

  @Id @UUIDAsIdIfNullGenerator UUID id;

  String categorySlug;
  String displayName;
}
