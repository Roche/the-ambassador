CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS project (
    id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(256) NOT NULL,
    excerpt VARCHAR(1024),
    languages VARCHAR(32) ARRAY,
    stars INTEGER NOT NULL DEFAULT 0,
    criticality_score REAL NOT NULL DEFAULT 0.0,
    activity_score REAL NOT NULL DEFAULT 0.0,
    total_score REAL NOT NULL DEFAULT 0.0,
    project JSONB NOT NULL
);

CREATE INDEX IF NOT EXISTS project_name_gin_idx ON project USING gin (name gin_trgm_ops);