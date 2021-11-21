package com.roche.ambassador.model.source

import com.roche.ambassador.model.project.Visibility
import java.time.LocalDate

object InvalidProjectCriteria {

    fun hasDefaultBranch(): CriterionVerifier = {
        it.defaultBranch != null
    }

    fun isRepositoryNotEmpty(): CriterionVerifier = {
        !it.empty
    }

    fun canCreateMergeRequest(): CriterionVerifier = {
        it.permissions?.canEveryoneCreatePullRequest ?: false
    }

    fun canForkProject(): CriterionVerifier = {
        it.permissions?.canEveryoneFork ?: false
    }

    fun hasVisibilityAtMost(visibility: Visibility): CriterionVerifier = {
        when (visibility) {
            Visibility.PUBLIC -> it.visibility == Visibility.PUBLIC
            Visibility.INTERNAL -> it.visibility != Visibility.PRIVATE
            Visibility.PRIVATE -> true
            else -> false
        }
    }

    fun excludeArchived(): CriterionVerifier = {
        !it.archived
    }

    fun hasActivityAfter(date: LocalDate): CriterionVerifier = {
        it.lastActivityDate != null && it.lastActivityDate.isAfter(date)
    }

    fun excludeForked(): CriterionVerifier = {
        !it.forked
    }

}
