package com.vulinh.data.dto.request;

import module java.base;

/** Shared editable fields used by post validation and normalization. */
public interface PostRequest<P extends PostRequest<P>> {

  String title();

  String excerpt();

  String postContent();

  String slug();

  UUID categoryId();

  Set<String> tags();

  P withTags(Set<String> tags);

  P withSlug(String slug);
}
