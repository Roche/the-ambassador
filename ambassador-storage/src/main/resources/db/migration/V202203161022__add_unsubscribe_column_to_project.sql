ALTER TABLE project ADD COLUMN IF NOT EXISTS subscribed BOOLEAN;
UPDATE project SET subscribed = true;
ALTER TABLE project ALTER COLUMN subscribed SET NOT NULL;