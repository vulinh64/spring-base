--liquibase formatted sql
--changeset vulinh:20240419-0005
INSERT INTO user_role_mapping
    (user_id, role_id)
VALUES ('00000000-0000-0000-0000-000000000001'::uuid, 'POWER_USER'),
       ('00000000-0000-0000-0000-000000000000'::uuid, 'ADMIN');