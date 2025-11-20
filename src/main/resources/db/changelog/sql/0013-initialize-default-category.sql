--liquibase formatted sql
--changeset vulinh:20241217-0001
INSERT INTO category
    (id, category_slug, display_name)
VALUES ('00000000-0000-0000-0000-000000000000'::uuid, 'uncategorized', 'Uncategorized');