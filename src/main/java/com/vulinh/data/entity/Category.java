package com.vulinh.data.entity;

import com.vulinh.configuration.UUIDIfNullStrategy;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.io.Serial;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@With
public class Category extends AbstractIdentifiable.StringAbstractIdentifiable {

  @Serial private static final long serialVersionUID = 106688923162808538L;

  @Id
  @GenericGenerator(name = UUIDIfNullStrategy.GENERATOR_NAME, type = UUIDIfNullStrategy.class)
  @GeneratedValue(generator = UUIDIfNullStrategy.GENERATOR_NAME)
  private String id;

  private String displayName;
}
