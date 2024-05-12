--liquibase formatted sql
--changeset vulinh:20240419-0002
CREATE TABLE "roles" (
	id varchar(50) NOT NULL,
	superiority int4 NOT NULL,
	CONSTRAINT role_pk PRIMARY KEY (id),
	CONSTRAINT role_unique UNIQUE (superiority)
);