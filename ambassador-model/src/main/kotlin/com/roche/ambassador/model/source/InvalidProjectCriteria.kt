package com.roche.ambassador.model.source

import com.roche.ambassador.model.Visibility
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
        visibility.isMoreStrictThan(it.visibility)
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
