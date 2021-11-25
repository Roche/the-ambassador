/* IN PROGRESS */
/* and locked*/
INSERT INTO indexing (indexing_lock, started_by, started_date, status, target, source)
VALUES (gen_random_uuid(), 'test', current_timestamp, 'IN_PROGRESS', '__ALL__', 'any');
INSERT INTO indexing (indexing_lock, started_by, started_date, status, target, source)
VALUES (gen_random_uuid(), 'test', current_timestamp, 'IN_PROGRESS', 'target', 'any');

/* and unlocked */
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp - INTERVAL '1 day', 'IN_PROGRESS', '__ALL__', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp - INTERVAL '1 days', 'IN_PROGRESS', '__ALL__', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp - INTERVAL '2 days', 'IN_PROGRESS', '__ALL__', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'target', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'target', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp, 'IN_PROGRESS', 'another target', 'any');
INSERT INTO indexing (started_by, started_date, status, target, source)
VALUES ('test', current_timestamp - INTERVAL '1 day', 'IN_PROGRESS', 'another darget', 'any');

/* FINISHED */
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', '__ALL__', 'any');
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', '__ALL__', 'any');
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', 'target', 'any');
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', 'target', 'any');
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp, 'test', current_timestamp, 'FINISHED', 'another target', 'any');
INSERT INTO indexing (finished_date, started_by, started_date, status, target, source)
VALUES (current_timestamp - INTERVAL '1 day', 'test', current_timestamp, 'FINISHED', 'another target', 'any');