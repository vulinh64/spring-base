--liquibase formatted sql
--changeset vulinh:20240419-0001
INSERT INTO "users"
(id, "email", username, full_name, "password", is_active, created_date_time, updated_date_time)
VALUES ('00000000-0000-0000-0000-000000000000'::uuid, 'admin@site.com', 'admin', 'Administrator',
        '$2a$12$grN.53C5Ze9RG8du6hupFehRnAs6nAeqidyywsMKP.h9r6AaiqprW', true, NOW(), NOW()),
       ('00000000-0000-0000-0000-000000000001'::uuid, 'power_user@site.com', 'power_user', 'Power User',
        '$2a$12$grN.53C5Ze9RG8du6hupFehRnAs6nAeqidyywsMKP.h9r6AaiqprW', true, NOW(), NOW());