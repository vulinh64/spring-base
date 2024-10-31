package com.vulinh.data.dto.event;

public sealed interface WithDocumentElasticsearchEvent
    permits PostDeletionElasticsearchEvent, PostElasticsearchEvent {}
