package com.roche.ambassador.gitlab

import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.ForkedProjectCriteria
import com.roche.gitlab.api.project.model.Project
import com.roche.gitlab.api.project.model.SimpleProject
import java.time.LocalDate

internal object GitLabForkedProjectCriteria : ForkedProjectCriteria<Project> {
    override fun includeAllWithForks(): CriterionVerifier<Project> = {
        true
    }

    override fun excludeAllWithForks(): CriterionVerifier<Project> = {
        it.whenForked { false }
    }

    override fun lastForkActivityBeforeParentMoreThan(days: Long): CriterionVerifier<Project> = {
        it.whenForked { parent ->
            parent.lastActivityAt?.isBefore(it.lastActivityAt?.minusDays(days)) ?: true
        }
    }

    override fun lastForkActivityNotEarlierThan(date: LocalDate): CriterionVerifier<Project> = {
        it.whenForked { _ ->
            it.lastActivityAt?.isAfter(date.atStartOfDay()) ?: true
        }
    }

    override fun forkHasStars(stars: Int): CriterionVerifier<Project> = {
        it.whenForked { _ ->
            val actualStars = it.starCount ?: 0
            actualStars >= stars
        }
    }

    private fun Project.whenForked(block: (SimpleProject) -> Boolean): Boolean {
        return if (this.isForked()) {
            block.invoke(this.forkedFrom!!)
        } else {
            true
        }
    }
}
