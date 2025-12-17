--liquibase formatted sql
--changeset vulinh:20250105-0001
CREATE TABLE user_session
(
    user_id              uuid        NOT NULL,
    session_id           uuid        NOT NULL,
    created_date_time    timestamptz NULL,
    updated_date_time    timestamptz NULL,
    expiration_date_time timestamptz NOT NULL,
    CONSTRAINT user_session_pk PRIMARY KEY (user_id, session_id)
);

CREATE INDEX user_session_user_id_idx ON user_session (user_id);

CREATE INDEX user_session_session_id_idx ON user_session (session_id);

CREATE INDEX user_session_expiration_date_idx ON user_session (expiration_date_time);
