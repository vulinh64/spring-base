--liquibase formatted sql
--changeset vulinh:20240419-0000
CREATE TABLE "users"
(
    id                             uuid         NOT NULL,
    username                       varchar(200) NOT NULL,
    full_name                      varchar(200) NULL,
    "email"                        varchar(200) NOT NULL,
    "password"                     varchar(72)  NOT NULL,
    is_active                      bool DEFAULT false NULL,
    password_reset_code            varchar(36) NULL,
    password_reset_code_expiration timestamptz NULL,
    user_registration_code         varchar(36) NULL,
    created_date                   timestamptz NULL,
    updated_date                   timestamptz NULL,
    CONSTRAINT user_pk PRIMARY KEY (id),
    CONSTRAINT user_unique UNIQUE (username),
    CONSTRAINT email_unique UNIQUE ("email")
);