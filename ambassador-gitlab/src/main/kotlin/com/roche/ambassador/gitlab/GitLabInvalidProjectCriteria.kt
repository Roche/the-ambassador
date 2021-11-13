package com.roche.ambassador.gitlab

import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.InvalidProjectCriteria
import com.roche.gitlab.api.project.model.Project
import com.roche.gitlab.api.model.Visibility as GitLabVisibility
import java.time.LocalDate

internal object GitLabInvalidProjectCriteria : InvalidProjectCriteria<Project> {
    override fun hasDefaultBranch(): CriterionVerifier<Project> = {
        it.defaultBranch != null
    }

    override fun isRepositoryNotEmpty(): CriterionVerifier<Project> = {
        !(it.emptyRepo ?: true)
    }

    override fun canCreateMergeRequest(): CriterionVerifier<Project> = {
        it.mergeRequestsAccessLevel?.canEveryoneAccess() ?: false
    }

    override fun canForkProject(): CriterionVerifier<Project> = {
        it.forkingAccessLevel?.canEveryoneAccess() ?: false
    }

    override fun hasVisibilityAtMost(visibility: Visibility): CriterionVerifier<Project> = {
        when(visibility) {
            Visibility.PUBLIC -> it.visibility == GitLabVisibility.PUBLIC
            Visibility.INTERNAL -> it.visibility != GitLabVisibility.PRIVATE
            Visibility.PRIVATE -> true
            else -> false
        }
    }

    override fun isNotArchived(): CriterionVerifier<Project> = {
        it.archived == null || !it.archived!!
    }

    override fun hasActivityAfter(date: LocalDate): CriterionVerifier<Project> = {
        it.lastActivityAt != null && it.lastActivityAt!!.isAfter(date.atStartOfDay())
    }
}
