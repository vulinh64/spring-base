--liquibase formatted sql
--changeset vulinh:20240419-0004
CREATE TABLE user_role_mapping
(
    user_id uuid        NOT NULL,
    role_id varchar(50) NOT NULL,
    CONSTRAINT user_role_mapping_pk PRIMARY KEY (user_id, role_id)
);

ALTER TABLE user_role_mapping
    ADD CONSTRAINT user_role_mapping_roles_fk FOREIGN KEY (role_id) REFERENCES roles (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE user_role_mapping
    ADD CONSTRAINT user_role_mapping_users_fk FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE CASCADE;