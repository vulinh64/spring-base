package com.vulinh.data.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import java.io.Serial;
import java.io.Serializable;
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
public class PostRevisionId implements Serializable {

  @Serial private static final long serialVersionUID = 944099295600818675L;

  public static final String POST_REVISION_SEQ = "post_revision_seq";
  public static final String POST_REVISION_SEQ_NAME = POST_REVISION_SEQ + "_generator";

  private UUID postId;

  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = POST_REVISION_SEQ_NAME)
  @SequenceGenerator(
      name = POST_REVISION_SEQ_NAME,
      sequenceName = POST_REVISION_SEQ,
      allocationSize = 1)
  private Long revisionNumber;
}
