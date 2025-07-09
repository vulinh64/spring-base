--liquibase formatted sql
--changeset vulinh:20250609-0000
CREATE extension IF NOT EXISTS pg_trgm;

CREATE INDEX category_display_name_trigram_idx ON category USING GIST (LOWER(display_name) gist_trgm_ops);

CREATE INDEX user_id_trigram_idx ON "users" USING GIST (LOWER(id::text) gist_trgm_ops);

CREATE INDEX user_username_trigram_idx ON "users" USING GIST (username gist_trgm_ops);

CREATE INDEX user_email_trigram_idx ON "users" USING GIST (email gist_trgm_ops);

CREATE INDEX user_full_name_trigram_idx ON "users" USING GIST (lower(full_name) gist_trgm_ops);