CREATE INDEX CONCURRENTLY project_visibility_tags_idx ON project((project ->> 'visibility'), (project -> 'tags'));
