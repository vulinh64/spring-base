package com.vulinh.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
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
public class Tag extends AbstractIdentifiable.StringAbstractIdentifiable {

  @Serial private static final long serialVersionUID = 5399822567855696869L;

  @Id @UuidGenerator private String id;

  private String displayName;

  public static Tag of(String displayName) {
    return Tag.builder().displayName(displayName).build();
  }
}
