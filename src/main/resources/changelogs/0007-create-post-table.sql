--liquibase formatted sql
--changeset vulinh:20240507-0002
CREATE TABLE post (
	id uuid NOT NULL,
	title varchar(5000) NOT NULL,
	slug varchar(5000) NOT NULL,
	excerpt varchar(500) NULL,
	post_content text NOT NULL,
	author_id uuid NULL,
	category_id uuid NULL DEFAULT '00000000-0000-0000-0000-000000000000',
	created_date timestamp NULL,
	updated_date timestamp NULL,
	updated_by uuid NULL,
	CONSTRAINT post_pk PRIMARY KEY (id),
	CONSTRAINT post_unique UNIQUE (slug),
	CONSTRAINT post_category_fk FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET DEFAULT ON UPDATE CASCADE,
	CONSTRAINT post_users_fk FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
	CONSTRAINT post_users_updated_by_fk FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE INDEX post_slug_idx ON public.post USING btree (slug);
CREATE INDEX post_title_idx ON public.post USING btree (title);