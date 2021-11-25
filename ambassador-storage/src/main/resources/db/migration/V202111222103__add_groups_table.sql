ALTER TABLE indexing ADD COLUMN source text;
UPDATE indexing SET source = '_unknown_';
ALTER TABLE indexing ALTER COLUMN source SET NOT NULL;

CREATE TABLE IF NOT EXISTS "group"
(
    id                BIGINT PRIMARY KEY  NOT NULL,
    name              VARCHAR(256)        NOT NULL,
    full_name         VARCHAR(1024)       NOT NULL,
    stars             INTEGER             NOT NULL DEFAULT 0,
    criticality_score REAL                NOT NULL DEFAULT 0.0,
    activity_score    REAL                NOT NULL DEFAULT 0.0,
    score             REAL                NOT NULL DEFAULT 0.0,
    "group"           JSONB               NOT NULL,
    last_indexed_date timestamp
);

ALTER TABLE "group"
    ADD COLUMN textsearch tsvector
        GENERATED ALWAYS AS (
                    setweight(to_tsvector('${language}', coalesce(name, '')), 'A') ||
                    setweight(to_tsvector('${language}', coalesce("group"->>'description', '')), 'A')
            ) STORED;

CREATE INDEX IF NOT EXISTS project_parent_id_index ON project (((project->'parent'->>'id')::bigint));

ALTER TABLE project DROP COLUMN IF EXISTS languages;
