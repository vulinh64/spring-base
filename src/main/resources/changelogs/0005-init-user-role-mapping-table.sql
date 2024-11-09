--liquibase formatted sql
--changeset vulinh:20240419-0005
INSERT INTO user_role_mapping
    (user_id, role_id)
VALUES ('3572035e-6485-4398-a505-bda703bc8c51', 'POWER_USER'),
       ('e36aa2ef-5542-460f-af01-a792b05e0843', 'USER'),
       ('e537261c-b45a-4613-be84-f928b99ee6ef', 'ADMIN');