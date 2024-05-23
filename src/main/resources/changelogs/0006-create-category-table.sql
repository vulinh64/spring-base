--liquibase formatted sql
--changeset vulinh:20240507-0001
CREATE TABLE category (
	id varchar(36) NOT NULL,
	category_slug varchar(500) NOT NULL,
	display_name varchar(500) NOT NULL,
	CONSTRAINT category_pk PRIMARY KEY (id),
	CONSTRAINT category_slug_unique UNIQUE (category_slug)
);
