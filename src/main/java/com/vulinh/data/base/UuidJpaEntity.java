package com.vulinh.data.base;

import module java.base;

// Every JPA entity MUST extend this class to use equals and hashCode!!!
public abstract class UuidJpaEntity extends AbstractIdentifiable<UUID> {

  @Serial private static final long serialVersionUID = -7023656451162094548L;
}
