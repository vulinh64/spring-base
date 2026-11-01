--liquibase formatted sql
--changeset vulinh:20261101-0012

ALTER TABLE post ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

--rollback ALTER TABLE post DROP COLUMN version;
