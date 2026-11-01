--liquibase formatted sql
--changeset vulinh:20261101-0013

-- Keep writes out while associations are rerouted and uniqueness is established.
LOCK TABLE tag, post_tag_mapping IN SHARE ROW EXCLUSIVE MODE;

-- Tag uses Hibernate Style.TIME UUIDs: the first six bytes of the least-significant
-- half contain the generation time. Sorting the complete UUID is not chronological.
-- Imported UUIDs from other generators have no reliable creation time in this schema.
CREATE TEMPORARY TABLE tag_duplicate_mapping ON COMMIT DROP AS
SELECT id AS duplicate_id, oldest_id
FROM (
    SELECT id,
           first_value(id) OVER (
               PARTITION BY display_name
               ORDER BY substring(uuid_send(id) FROM 9 FOR 6), id
           ) AS oldest_id
    FROM tag
) ranked_tags
WHERE id <> oldest_id;

-- Insert before deleting: a post may already reference both copies of the tag.
INSERT INTO post_tag_mapping (post_id, tag_id)
SELECT DISTINCT mapping.post_id, duplicates.oldest_id
FROM post_tag_mapping mapping
JOIN tag_duplicate_mapping duplicates ON duplicates.duplicate_id = mapping.tag_id
ON CONFLICT (post_id, tag_id) DO NOTHING;

DELETE FROM post_tag_mapping mapping
USING tag_duplicate_mapping duplicates
WHERE mapping.tag_id = duplicates.duplicate_id;

DELETE FROM tag
USING tag_duplicate_mapping duplicates
WHERE tag.id = duplicates.duplicate_id;

-- The unique constraint creates a B-tree index that replaces the original index.
ALTER TABLE tag ADD CONSTRAINT tag_display_name_unique UNIQUE (display_name);
DROP INDEX tag_display_name_idx;

-- Rollback restores the indexes only; merged tag identities cannot be reconstructed.
--rollback CREATE INDEX tag_display_name_idx ON tag USING btree (display_name);
--rollback ALTER TABLE tag DROP CONSTRAINT tag_display_name_unique;
