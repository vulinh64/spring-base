package com.vulinh.data.entity;

import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
public class Tag extends UuidJpaEntity {

  @Serial private static final long serialVersionUID = 5399822567855696869L;

  @Id
  @UuidGenerator(style = Style.TIME)
  private UUID id;

  private String displayName;
}
