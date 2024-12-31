package com.vulinh.data.entity;

import com.vulinh.data.base.AbstractIdentifiable;
import com.vulinh.data.entity.ids.UserSessionId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.*;
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
public class UserSession extends AbstractIdentifiable<UserSessionId> {

  @Serial private static final long serialVersionUID = -7459401877851544734L;

  @EmbeddedId private UserSessionId id;

  private LocalDateTime expirationDate;

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime updatedDate;
}
