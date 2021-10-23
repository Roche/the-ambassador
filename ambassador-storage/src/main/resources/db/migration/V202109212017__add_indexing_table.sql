CREATE TABLE IF NOT EXISTS indexing
(
    id                        UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    started_by                VARCHAR(256)     NOT NULL,
    started_date              TIMESTAMP        NOT NULL,
    finished_date             TIMESTAMP,
    status                    VARCHAR(20)      NOT NULL,
    target                    VARCHAR(50)      NOT NULL,
    stats_total_projects_read BIGINT,
    stats_indexed_projects    BIGINT,
    stats_excluded_errors     BIGINT,
    stats_total_errors        BIGINT,
    stats_exclusions          JSONB,
    stats_errors              JSONB,
    indexing_lock             UUID
);

CREATE UNIQUE INDEX IF NOT EXISTS indexing_lock_for_target_idx
    ON indexing (target)
    WHERE indexing_lock IS NOT NULL;

CREATE INDEX IF NOT EXISTS indexing_target_idx ON indexing (target);