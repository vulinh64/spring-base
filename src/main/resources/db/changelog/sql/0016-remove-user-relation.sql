--liquibase formatted sql
--changeset vulinh:20251127-0000
ALTER TABLE post DROP CONSTRAINT post_users_fk;
ALTER TABLE post DROP CONSTRAINT post_users_updated_by_fk;
ALTER TABLE comment DROP CONSTRAINT comment_user_fk;
