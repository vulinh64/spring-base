DELETE FROM comment_revision
WHERE post_id = 'c0a802df-9fe5-1c2a-819f-e5ec41120003';

DELETE FROM comment
WHERE post_id = 'c0a802df-9fe5-1c2a-819f-e5ec41120003';

DELETE FROM post_revision
WHERE post_id = 'c0a802df-9fe5-1c2a-819f-e5ec41120003';

DELETE FROM post_tag_mapping
WHERE post_id = 'c0a802df-9fe5-1c2a-819f-e5ec41120003';

DELETE FROM post
WHERE id = 'c0a802df-9fe5-1c2a-819f-e5ec41120003';

DELETE FROM tag
WHERE id = 'c0a802df-9fe5-1c2a-819f-e5ec41120004';

ALTER SEQUENCE post_revision_seq RESTART WITH 2;
ALTER SEQUENCE comment_revision_seq RESTART WITH 2;
