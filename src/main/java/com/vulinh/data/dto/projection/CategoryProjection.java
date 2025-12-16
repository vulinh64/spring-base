package com.vulinh.data.dto.projection;

import module java.base;

import com.vulinh.data.base.Identifiable;

public interface CategoryProjection extends Identifiable<UUID> {

  String getDisplayName();
}
