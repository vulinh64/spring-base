package com.vulinh.data.entity;

import com.vulinh.configuration.UUIDAsIdIfNullGenerator;
import com.vulinh.data.entity.AbstractIdentifiable.UUIDJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.util.UUID;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
public class Category extends UUIDJpaEntity {

  @Serial private static final long serialVersionUID = 106688923162808538L;

  @Id @UUIDAsIdIfNullGenerator private UUID id;

  private String categorySlug;
  private String displayName;
}
