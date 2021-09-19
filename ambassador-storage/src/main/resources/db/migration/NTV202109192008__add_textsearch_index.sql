CREATE INDEX CONCURRENTLY textsearch_idx ON project USING GIN (textsearch);
CREATE INDEX CONCURRENTLY visibility_idx ON project((project->>'visibility'));

SET work_mem TO '20 MB';