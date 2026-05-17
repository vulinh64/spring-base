--liquibase formatted sql
--changeset vulinh:20260322-0002
CREATE TABLE author
(
    id                uuid         NOT NULL,
    username          varchar(200) NOT NULL,
    display_name      varchar(200) NULL,
    email             varchar(200) NULL,
    created_date_time timestamptz  NULL,
    updated_date_time timestamptz  NULL,
    CONSTRAINT author_pk PRIMARY KEY (id),
    CONSTRAINT author_username_unique UNIQUE (username)
);
