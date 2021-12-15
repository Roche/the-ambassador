package com.roche.ambassador.fake

import com.roche.ambassador.model.source.Issue
import com.roche.ambassador.model.source.IssuesManager
import java.util.*

internal class FakeIssuesManager(maxSize: Int = 100) : IssuesManager {

    private val datastore = FakeExternalDatastore.withLong<Issue>(maxSize)

    override suspend fun get(id: Long, projectId: Long): Optional<Issue> = datastore.read(id)

    override suspend fun create(issue: Issue): Issue = datastore.create(issue)

    override suspend fun update(issue: Issue): Issue = datastore.update(issue)
}