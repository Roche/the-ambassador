ALTER TABLE project_statistics_history
    DROP CONSTRAINT project_statistics_history_project_id_fkey,
    ADD CONSTRAINT project_statistics_history_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES project (id) ON DELETE CASCADE;