CREATE TABLE IF NOT EXISTS languages_lookup
(
    id    UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    name  VARCHAR(256)     NOT NULL,
    count BIGINT           NOT NULL
);

CREATE INDEX IF NOT EXISTS languages_lookup_idx ON languages_lookup (name, count);