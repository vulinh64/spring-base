--liquibase formatted sql
--changeset vulinh:20240507-0006
INSERT INTO post
(id, title, slug, excerpt, post_content, author_id, category_id, created_date, updated_date)
VALUES
('4cf207fc-651b-4c00-b23f-a22a79460827',
'Test blog post',
'test-blog-post',
'This is a test excerpt',
E'This is a post!\n\nThis is a new line\n\n```java\nThis is markdown!\n```',
'e537261c-b45a-4613-be84-f928b99ee6ef',
'641ea43f-c426-49ad-bd7b-5ec551a6fa12',
NOW(),
NOW());

INSERT INTO tag
(id, display_name)
VALUES
('412d9826-abe9-4402-bf59-3140e3968ce4',
'hello world');

INSERT INTO post_tag_mapping
(post_id, tag_id)
VALUES
('4cf207fc-651b-4c00-b23f-a22a79460827',
'412d9826-abe9-4402-bf59-3140e3968ce4');

