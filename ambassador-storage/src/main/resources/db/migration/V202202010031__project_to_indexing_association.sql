ALTER TABLE project
ADD COLUMN IF NOT EXISTS last_indexing_id UUID REFERENCES indexing(id);