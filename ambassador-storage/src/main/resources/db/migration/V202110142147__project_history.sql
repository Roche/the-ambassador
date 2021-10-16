CREATE TABLE IF NOT EXISTS project_history
(
    id           UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    project_id   INTEGER          NOT NULL REFERENCES project(id),
    project      JSONB            NOT NULL,
    indexed_date TIMESTAMP        NOT NULL
);

CREATE INDEX IF NOT EXISTS project_id_idx ON project_history(id, project_id);