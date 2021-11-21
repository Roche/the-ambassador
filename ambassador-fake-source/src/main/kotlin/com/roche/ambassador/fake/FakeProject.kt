package com.roche.ambassador.fake

import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.model.stats.Statistics
import java.time.LocalDate

class FakeProject(
    val id: Long,
    val name: String,
    var visibility: Visibility,
    var createdDate: LocalDate,
    var tags: List<String>,
    var url: String? = null,
    var avatarUrl: String? = null,
    var description: String? = null,
    var defaultBranch: String? = null,
    var stats: Statistics = Statistics.no(),
    var lastUpdatedDate: LocalDate? = null,
    var forked: Boolean = false,
    var emptyRepository: Boolean = false,
    var canFork: Boolean = true,
    var canCreatePullRequest: Boolean = true
) {

    fun asProject(): Project = Project(
        id, url, avatarUrl, name,
        description, tags, visibility, defaultBranch,
        false, emptyRepository, forked, stats,
        createdDate, lastUpdatedDate, Permissions(canFork, canCreatePullRequest)
    )
}
