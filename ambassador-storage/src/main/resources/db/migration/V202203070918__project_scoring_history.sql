-- cleanup project history
DROP TABLE IF EXISTS project_history;

-- setup project statistics history
CREATE TABLE IF NOT EXISTS project_statistics_history
(
    id          UUID      NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id  BIGINT    NOT NULL REFERENCES project (id),
    stats       JSONB     NOT NULL,
    record_date TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS project_statistics_history_project_id_date_idx ON project_statistics_history (project_id, record_date);