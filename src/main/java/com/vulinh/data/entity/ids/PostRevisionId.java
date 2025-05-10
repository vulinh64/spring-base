package com.vulinh.data.entity.ids;

import com.vulinh.data.base.UUIDRevisionId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import java.io.Serial;
import java.util.UUID;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@With
public class PostRevisionId implements UUIDRevisionId {

  @Serial private static final long serialVersionUID = 944099295600818675L;

  public static Object of(UUID postId, long revisionNumber) {
    return new PostRevisionId(postId, revisionNumber);
  }

  public static final String TABLE_NAME = "post";
  public static final String REVISION_SEQ = TABLE_NAME + "_revision_seq";
  public static final String REVISION_SEQ_NAME = REVISION_SEQ + "_generator";

  private UUID postId;

  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = REVISION_SEQ_NAME)
  @SequenceGenerator(name = REVISION_SEQ_NAME, sequenceName = REVISION_SEQ, allocationSize = 1)
  private Long revisionNumber;

  @Override
  public UUID getId() {
    return postId;
  }
}
