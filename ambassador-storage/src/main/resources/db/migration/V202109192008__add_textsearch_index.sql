CREATE INDEX CONCURRENTLY textsearch_idx ON project USING GIN (textsearch);
CREATE INDEX CONCURRENTLY visibility_idx ON project((project->>'visibility'));