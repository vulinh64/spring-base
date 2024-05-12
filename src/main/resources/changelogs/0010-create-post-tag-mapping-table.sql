--liquibase formatted sql
--changeset vulinh:20240507-0005
CREATE TABLE post_tag_mapping (
	post_id varchar(36) NOT NULL,
	tag_id varchar(36) NOT NULL,
	CONSTRAINT post_tag_mapping_pk PRIMARY KEY (post_id, tag_id),
	CONSTRAINT post_tag_mapping_post_fk FOREIGN KEY (post_id) REFERENCES post(id) ON UPDATE CASCADE,
	CONSTRAINT post_tag_mapping_tag_fk FOREIGN KEY (tag_id) REFERENCES tag(id) ON UPDATE CASCADE
);
