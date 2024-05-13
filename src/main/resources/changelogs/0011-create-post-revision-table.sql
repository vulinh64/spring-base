--liquibase formatted sql
--changeset vulinh:20240510-0001
CREATE TABLE post_revision (
	post_id varchar(36) NOT NULL,
	revision_number int8 NOT NULL,
	revision_type int4 NOT NULL,
	title varchar(5000) NULL,
	slug varchar(5000) NULL,
	excerpt varchar(5000) NULL,
	post_content text NULL,
	author_id varchar(36) NULL,
	category_id varchar NULL DEFAULT '641ea43f-c426-49ad-bd7b-5ec551a6fa12',
	tags text NULL,
	revision_created_date varchar NULL,
	revision_created_by varchar(36) NULL,
	CONSTRAINT post_revision_pk PRIMARY KEY (post_id, revision_number)
);
CREATE INDEX post_revision_slug_idx ON post_revision USING btree (slug);
CREATE INDEX post_revision_title_idx ON post_revision USING btree (title);

CREATE SEQUENCE IF NOT EXISTS post_revision_seq
	INCREMENT BY 1
	MINVALUE 2
	MAXVALUE 9223372036854775807
	START 2
	CACHE 1
	NO CYCLE;

ALTER SEQUENCE IF EXISTS post_revision_seq RESTART 2;