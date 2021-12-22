DROP INDEX IF EXISTS textsearch_idx;

ALTER TABLE project DROP COLUMN textsearch;

ALTER TABLE project
    ADD COLUMN textsearch tsvector
        GENERATED ALWAYS AS (
                    setweight(to_tsvector('${language}', coalesce(name, '')), 'A') ||
                    setweight(to_tsvector('${language}', coalesce(project->>'description', '')), 'B') ||
                    setweight(to_tsvector('${language}', coalesce(project->>'tags', '')), 'C') ||
                    setweight(to_tsvector('${language}', coalesce(project->'features'->'readme'->>'excerpt', '')), 'D')
            ) STORED;