package com.roche.ambassador.model.source

import java.util.*

interface IssuesManager {

    suspend fun get(id: Long, projectId: Long): Optional<Issue>
    suspend fun create(issue: Issue): Issue
    suspend fun update(issue: Issue): Issue
    suspend fun createOrUpdate(issue: Issue): Issue = if (issue.getId() == null) {
        create(issue)
    } else {
        update(issue)
    }

}