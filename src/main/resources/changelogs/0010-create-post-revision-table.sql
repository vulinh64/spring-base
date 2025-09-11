--liquibase formatted sql
--changeset vulinh:20240510-0001
CREATE TABLE post_revision
(
    post_id               uuid NOT NULL,
    revision_number       int8 NOT NULL,
    revision_type         int4 NOT NULL,
    title                 varchar(5000) NULL,
    slug                  varchar(5000) NULL,
    excerpt               varchar(5000) NULL,
    post_content          text NULL,
    author_id             uuid NULL,
    category_id           uuid NULL DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tags                  text NULL,
    revision_created_date timestamptz NULL,
    revision_created_by   uuid NULL,
    CONSTRAINT post_revision_pk PRIMARY KEY (post_id, revision_number)
);

CREATE INDEX post_revision_author_id_idx ON post_revision USING btree (author_id);

CREATE INDEX post_revision_category_id_idx ON post_revision USING btree (category_id);

CREATE INDEX post_revision_revision_created_by_idx ON post_revision USING btree (revision_created_by);

CREATE INDEX post_revision_revision_created_date_idx ON post_revision USING btree (revision_created_date);

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