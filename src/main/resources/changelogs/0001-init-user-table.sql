--liquibase formatted sql
--changeset vulinh:20240419-0001
INSERT INTO "users"
(id, "email", username, full_name, "password", is_active, created_date, updated_date)
VALUES
('e537261c-b45a-4613-be84-f928b99ee6ef', 'admin@site.com', 'admin', 'Administrator', '$2a$12$grN.53C5Ze9RG8du6hupFehRnAs6nAeqidyywsMKP.h9r6AaiqprW', true, NOW(), NOW()),
('3572035e-6485-4398-a505-bda703bc8c51', 'power_user@site.com', 'power_user', 'Power User', '$2a$12$grN.53C5Ze9RG8du6hupFehRnAs6nAeqidyywsMKP.h9r6AaiqprW', true, NOW(), NOW()),
('e36aa2ef-5542-460f-af01-a792b05e0843', 'user@site.com', 'user', 'User', '$2a$12$grN.53C5Ze9RG8du6hupFehRnAs6nAeqidyywsMKP.h9r6AaiqprW', true, NOW(), NOW());