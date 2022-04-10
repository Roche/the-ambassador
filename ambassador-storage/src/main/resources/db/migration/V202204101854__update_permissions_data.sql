UPDATE project
SET project['permissions']['forks'] = (CASE
                                            WHEN (project['permissions']['canEveryoneFork'])::bool = true THEN '"PUBLIC"'
                                            ELSE '"PRIVATE"'
                                       END)::jsonb,
    project['permissions']['pullRequests'] = (CASE
                                                    WHEN (project['permissions']['canEveryoneCreatePullRequest'])::bool = true THEN '"PUBLIC"'
                                                    ELSE '"PRIVATE"'
                                              END)::jsonb,
    project['permissions']['ci']                = '"UNKNOWN"',
    project['permissions']['containerRegistry'] = '"UNKNOWN"',
    project['permissions']['issues']            = '"UNKNOWN"',
    project['permissions']['repository']        = '"UNKNOWN"'
;

UPDATE project
SET project['permissions'] = project['permissions'] - 'canEveryoneFork';

UPDATE project
SET project['permissions'] = project['permissions'] - 'canEveryoneCreatePullRequest';