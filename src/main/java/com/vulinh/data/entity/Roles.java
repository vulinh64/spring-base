package com.vulinh.data.entity;

import com.vulinh.constant.UserRole;
import jakarta.persistence.*;
import java.io.Serial;
import java.util.Comparator;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Roles extends AbstractIdentifiable<UserRole> {

  public static final Comparator<Roles> COMPARATOR =
      Comparator.comparing(Roles::getSuperiority, Comparator.nullsLast(Comparator.reverseOrder()));

  @Serial private static final long serialVersionUID = 6275088415576977325L;

  @Id
  @Enumerated(EnumType.STRING)
  private UserRole id;

  private int superiority;
}
