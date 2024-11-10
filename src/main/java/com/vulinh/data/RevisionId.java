package com.vulinh.data;

import java.io.Serializable;
import java.util.UUID;

public interface RevisionId<I> extends Serializable {

  I getId();

  Number getRevisionNumber();

  interface UUIDRevisionId extends RevisionId<UUID> {}
}
