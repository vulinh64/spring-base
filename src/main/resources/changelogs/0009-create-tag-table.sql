--liquibase formatted sql
--changeset vulinh:20240507-0004
CREATE TABLE tag (
	id varchar(36) NOT NULL,
	display_name varchar(1000) NOT NULL,
	CONSTRAINT tag_pk PRIMARY KEY (id)
);

CREATE INDEX tag_display_name_idx ON tag USING btree (display_name);