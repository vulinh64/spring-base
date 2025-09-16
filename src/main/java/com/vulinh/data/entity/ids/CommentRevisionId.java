package com.vulinh.data.entity.ids;

import module java.base;

import com.vulinh.data.base.UUIDRevisionId;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
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
public class CommentRevisionId implements UUIDRevisionId {

  @Serial private static final long serialVersionUID = 8157034868720893573L;

  public static final String TABLE_NAME = "comment";
  public static final String REVISION_SEQ = TABLE_NAME + "_revision_seq";
  public static final String REVISION_SEQ_NAME = REVISION_SEQ + "_generator";

  private UUID commentId;

  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = REVISION_SEQ_NAME)
  @SequenceGenerator(name = REVISION_SEQ_NAME, sequenceName = REVISION_SEQ, allocationSize = 1)
  private Long revisionNumber;

  @Override
  public UUID getId() {
    return commentId;
  }
}
