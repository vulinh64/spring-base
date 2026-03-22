--liquibase formatted sql
--changeset vulinh:20260322-0001
ALTER TABLE post ADD CONSTRAINT post_slug_unique UNIQUE (slug);
