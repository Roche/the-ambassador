DROP INDEX IF EXISTS visibility_idx;
CREATE INDEX CONCURRENTLY project_visibility_tags_idx ON project((project ->> 'visibility'), (project -> 'tags'));

DROP INDEX IF EXISTS project_id_idx;