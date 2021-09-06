ALTER TABLE project
    ADD COLUMN last_indexed_date timestamp;
UPDATE project
SET last_indexed_date = current_timestamp;
ALTER TABLE project
    ALTER COLUMN last_indexed_date SET NOT NULL;