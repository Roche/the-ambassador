ALTER TABLE project ADD COLUMN source VARCHAR(256);
UPDATE project SET source = (select source from indexing where id = project.last_indexing_id) where last_indexing_id is not null;
UPDATE project SET source = '_unknown_' WHERE source IS NULL;