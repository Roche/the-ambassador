CREATE TABLE IF NOT EXISTS advisory_message
(
    id           UUID PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    project_id   BIGINT           NOT NULL,
    reference_id BIGINT           NOT NULL, /* nothing more is needed here, because content can always be read from source */
    source       VARCHAR(30)      NOT NULL,
    name         VARCHAR(30)      NOT NULL,
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    closed_date  TIMESTAMP,
    deleted      BOOLEAN          NOT NULL DEFAULT false,
    type         VARCHAR(20)      NOT NULL
);

CREATE INDEX IF NOT EXISTS advisory_messages_idx ON advisory_message (project_id, source);