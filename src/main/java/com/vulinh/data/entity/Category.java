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
public class Category extends AbstractIdentifiable.StringAbstractIdentifiable {

  @Serial private static final long serialVersionUID = 106688923162808538L;

  @Id @UuidGenerator private String id;

  private String displayName;
}
