DROP INDEX IF EXISTS project_name_gin_idx;
ALTER TABLE project DROP COLUMN IF EXISTS excerpt;
DROP EXTENSION IF EXISTS pg_trgm CASCADE;

ALTER TABLE project
    ADD COLUMN textsearch tsvector
        GENERATED ALWAYS AS (
                setweight(to_tsvector('${language}', coalesce(name, '')), 'A') ||
                setweight(to_tsvector('${language}', coalesce(project->>'description', '')), 'B') ||
                setweight(to_tsvector('${language}', coalesce(project->>'tags', '')), 'C')
            ) STORED;
