--liquibase formatted sql
--changeset vulinh:20260322-0003

-- Add tsvector column for full-text search (title + content combined)
ALTER TABLE post ADD COLUMN search_vector tsvector;

-- Populate existing rows
UPDATE post SET search_vector =
    setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(excerpt, '')), 'B') ||
    setweight(to_tsvector('english', coalesce(post_content, '')), 'C');

-- GIN index for fast full-text lookups
CREATE INDEX post_search_vector_idx ON post USING gin (search_vector);

--changeset vulinh:20260322-0004 splitStatements:false

-- Auto-update trigger: keeps search_vector in sync on INSERT or UPDATE
CREATE OR REPLACE FUNCTION post_search_vector_update() RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('english', coalesce(NEW.title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(NEW.excerpt, '')), 'B') ||
        setweight(to_tsvector('english', coalesce(NEW.post_content, '')), 'C');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER post_search_vector_trigger
    BEFORE INSERT OR UPDATE OF title, excerpt, post_content ON post
    FOR EACH ROW
    EXECUTE FUNCTION post_search_vector_update();
