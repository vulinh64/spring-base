--liquibase formatted sql
--changeset vulinh:20241111-0002
CREATE TABLE comment_revision
(
    comment_id            uuid NOT NULL,
    revision_number       int8 NOT NULL,
    revision_type         int4 NOT NULL,
    "content"             text NOT NULL,
    revision_created_date timestamp NULL,
    revision_created_by   uuid NULL,
    post_id               uuid NULL,
    CONSTRAINT comment_revision_pk PRIMARY KEY (comment_id, revision_number)
);

CREATE INDEX comment_revision_revision_type_idx ON comment_revision (revision_type);

CREATE INDEX comment_revision_revision_created_date_idx ON comment_revision (revision_created_date);

CREATE INDEX comment_revision_revision_created_by_idx ON comment_revision (revision_created_by);

CREATE INDEX comment_revision_post_id_idx ON comment_revision (post_id);

CREATE SEQUENCE IF NOT EXISTS comment_revision_seq
    INCREMENT BY 1
    MINVALUE 2
    MAXVALUE 9223372036854775807
    START 2
    CACHE 1
    NO CYCLE;

ALTER SEQUENCE IF EXISTS comment_revision_seq RESTART 2;
