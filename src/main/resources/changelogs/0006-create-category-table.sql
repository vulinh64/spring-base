--liquibase formatted sql
--changeset vulinh:20240507-0001
CREATE TABLE category (
	id varchar(36) NOT NULL,
	display_name varchar(100) NOT NULL,
	CONSTRAINT category_pk PRIMARY KEY (id)
);
