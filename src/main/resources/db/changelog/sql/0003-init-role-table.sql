--liquibase formatted sql
--changeset vulinh:20240419-0003
INSERT INTO "roles"
    (id, superiority)
VALUES ('ADMIN', 2147483647),
       ('POWER_USER', 2147483646),
       ('USER', 0),
       ('INVALID', -2147483647);