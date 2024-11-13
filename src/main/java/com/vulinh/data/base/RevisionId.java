package com.vulinh.data.base;

import java.io.Serializable;

public interface RevisionId<I> extends Serializable {

  I getId();

  Long getRevisionNumber();
}
