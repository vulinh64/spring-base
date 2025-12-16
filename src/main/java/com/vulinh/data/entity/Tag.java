package com.vulinh.data.entity;

import module java.base;

import com.vulinh.data.base.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Tag extends AbstractEntity<UUID> {

  @Serial private static final long serialVersionUID = 5399822567855696869L;

  @Id
  @UuidGenerator(style = Style.TIME)
  UUID id;

  String displayName;
}
