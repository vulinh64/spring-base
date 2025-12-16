--liquibase formatted sql
--changeset vulinh:20241111-0001
CREATE TABLE comment
(
    id                uuid NOT NULL,
    "content"         text NOT NULL,
    post_id           uuid NOT NULL,
    created_date_time timestamptz NULL,
    updated_date_time timestamptz NULL,
    created_by        uuid NOT NULL,
    updated_by        uuid NOT NULL,
    CONSTRAINT comment_pk PRIMARY KEY (id),
    CONSTRAINT comment_post_fk FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT comment_user_fk FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE INDEX comment_post_id_idx ON comment (post_id);

CREATE INDEX comment_created_date_idx ON comment (created_date_time);

CREATE INDEX comment_updated_date_idx ON comment (updated_date_time);

CREATE INDEX comment_created_by_idx ON comment (created_by);

CREATE INDEX comment_updated_by_idx ON comment (updated_by);
