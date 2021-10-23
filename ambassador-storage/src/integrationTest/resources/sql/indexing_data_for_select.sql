/* IN PROGRESS */
/* and locked*/
INSERT INTO indexing (indexing_lock, started_by, started_date, status, target)
VALUES (gen_random_uuid(), 'test', current_timestamp, 'IN_PROGRESS', '__ALL__');
INSERT INTO indexing (indexing_lock, started_by, started_date, status, target)
VALUES (gen_random_uuid(), 'test', current_timestamp, 'IN_PROGRESS', 'target');

/* and unlocked */
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp - INTERVAL '1 day', 'IN_PROGRESS', '__ALL__');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp - INTERVAL '1 days', 'IN_PROGRESS', '__ALL__');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp - INTERVAL '2 days', 'IN_PROGRESS', '__ALL__');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'target');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'target');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'another target');
INSERT INTO indexing (started_by, started_date, status, target)
VALUES ('test', current_timestamp - INTERVAL '1 day', 'IN_PROGRESS', 'another darget');

/* FINISHED */
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', '__ALL__');
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', '__ALL__');
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', 'target');
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', 'target');
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', 'another target');
INSERT INTO indexing (finished_date, started_by, started_date, status, target)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', 'another target');