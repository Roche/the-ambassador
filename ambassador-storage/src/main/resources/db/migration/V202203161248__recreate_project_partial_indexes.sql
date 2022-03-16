CREATE INDEX CONCURRENTLY project_textsearch_idx ON project USING GIN (textsearch) WHERE subscribed = true;
CREATE INDEX CONCURRENTLY project_visibility_topics_idx ON project((project ->> 'visibility'), (project -> 'topics')) WHERE subscribed = true;
