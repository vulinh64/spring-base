--liquibase formatted sql
--changeset vulinh:20250609-0000
CREATE extension IF NOT EXISTS pg_trgm;

CREATE INDEX category_display_name_trigram_idx ON category USING GIST (LOWER(display_name) gist_trgm_ops);
