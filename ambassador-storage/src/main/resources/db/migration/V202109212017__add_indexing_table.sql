CREATE TABLE IF NOT EXISTS indexing
(
    id                        UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    started_by                VARCHAR(256)     NOT NULL,
    started_date              TIMESTAMP        NOT NULL,
    finished_date             TIMESTAMP,
    status                    VARCHAR(20)      NOT NULL,
    target                    VARCHAR(50),
    stats_total_projects_read INTEGER,
    stats_indexed_projects    INTEGER,
    stats_excluded_errors     INTEGER,
    stats_total_errors        INTEGER,
    stats_exclusions          JSONB,
    stats_errors              JSONB,
    indexing_lock             UUID
);

CREATE UNIQUE INDEX IF NOT EXISTS indexing_lock_for_target_idx
    ON indexing ((target IS NULL))
    WHERE indexing_lock IS NOT NULL;

CREATE INDEX IF NOT EXISTS indexing_target_idx ON indexing(target);