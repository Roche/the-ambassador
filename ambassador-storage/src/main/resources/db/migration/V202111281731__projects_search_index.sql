CREATE INDEX CONCURRENTLY project_visibility_topics_idx ON project((project ->> 'visibility'), (project -> 'topics'));
