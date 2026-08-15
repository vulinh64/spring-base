INSERT INTO tag (id, display_name)
VALUES ('c0a802df-9fe5-1c2a-819f-e5ec41120004', 'existing tag');

INSERT INTO post (
  id, title, slug, excerpt, post_content, author_id, category_id,
  created_date_time, updated_date_time, updated_by)
VALUES (
  'c0a802df-9fe5-1c2a-819f-e5ec41120003', 'Original Post', 'original-post',
  'Original excerpt', 'Original content', 'b947e52c-d0b7-4c1d-9d62-3af33b2d968f',
  '00000000-0000-0000-0000-000000000000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
  'b947e52c-d0b7-4c1d-9d62-3af33b2d968f');

INSERT INTO post_tag_mapping (post_id, tag_id)
VALUES ('c0a802df-9fe5-1c2a-819f-e5ec41120003', 'c0a802df-9fe5-1c2a-819f-e5ec41120004');
