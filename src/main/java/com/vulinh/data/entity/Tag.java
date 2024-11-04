package com.vulinh.data.entity;

import com.vulinh.data.entity.AbstractIdentifiable.UUIDJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
public class Tag extends UUIDJpaEntity {

  @Serial private static final long serialVersionUID = 5399822567855696869L;

  @Id @UuidGenerator private UUID id;

  private String displayName;

  public static Tag of(String displayName) {
    return Tag.builder().displayName(displayName).build();
  }
}
