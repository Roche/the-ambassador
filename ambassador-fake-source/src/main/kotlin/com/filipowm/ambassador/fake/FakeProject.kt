package com.filipowm.ambassador.fake

import com.filipowm.ambassador.model.feature.Features
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.ambassador.model.stats.Statistics
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
    var stats: Statistics = Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0),
    var lastUpdatedDate: LocalDate? = null,
    var forked: Boolean = false,
    var emptyRepository: Boolean = false
) {

    fun asProject(): Project = Project(
        id, url, avatarUrl, name, description, tags, visibility, defaultBranch, stats, createdDate, lastUpdatedDate, Features()
    )
}
