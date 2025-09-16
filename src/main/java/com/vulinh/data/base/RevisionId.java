package com.vulinh.data.base;

import module java.base;

public interface RevisionId<I> extends Serializable {

  I getId();

  Long getRevisionNumber();
}
