package com.roche.ambassador.configuration.properties

import com.roche.ambassador.model.Visibility
import java.time.Duration
import java.time.LocalDateTime
import javax.validation.constraints.Min

data class IndexingCriteriaProperties(
    val projects: Projects = Projects(),
    val personalProjects: PersonalProjects = PersonalProjects()
) {

    data class Projects(
        val groups: List<String> = listOf(),
        val excludeArchived: Boolean = true,
        val excludeForks: Boolean = true,
        val maxVisibility: Visibility = Visibility.INTERNAL,
        val lastActivityWithin: Duration? = Duration.ofDays(365),
        val mustHaveDefaultBranch: Boolean = true,
        val mustHaveNotEmptyRepo: Boolean = true,
        val mustBeAbleToCreateMergeRequest: Boolean = true,
        val mustBeAbleToFork: Boolean = true
    ) {
        val lastActivityAfter: LocalDateTime? = if (lastActivityWithin != null) {
            LocalDateTime.now().minus(lastActivityWithin)
        } else {
            null
        }
    }

    data class PersonalProjects(
        val excludeAll: Boolean = false,
        @Min(0) val mustHaveAtLeastStars: Int = 0
    )
}

