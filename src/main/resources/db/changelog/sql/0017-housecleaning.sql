--liquibase formatted sql
--changeset vulinh:20251127-0001
ALTER TABLE user_role_mapping DROP CONSTRAINT user_role_mapping_roles_fk;
ALTER TABLE user_role_mapping DROP CONSTRAINT user_role_mapping_users_fk;

DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS user_session;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_role_mapping;