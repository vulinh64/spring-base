package com.vulinh.data.entity;

import com.vulinh.data.base.DateTimeAuditable;
import com.vulinh.data.base.UuidJpaEntity;
import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class Users extends UuidJpaEntity implements DateTimeAuditable {

  @Serial private static final long serialVersionUID = -2867497192634401616L;

  @Id
  @UuidGenerator(style = Style.TIME)
  private UUID id;

  private String username;
  private String fullName;
  private String email;

  @ToString.Exclude private String password;

  @Builder.Default private Boolean isActive = false;

  @ToString.Exclude private String passwordResetCode;

  @ToString.Exclude private LocalDateTime passwordResetCodeExpiration;

  @ToString.Exclude private String userRegistrationCode;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;

  @ManyToMany
  @JoinTable(
      name = "user_role_mapping",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @ToString.Exclude
  private Set<Roles> userRoles;
}
