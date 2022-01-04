package com.roche.ambassador.model.source

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.group.Group
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

    fun excludeGroups(groups: List<String>): CriterionVerifier = {
        val parent = it.parent
        if (parent != null) {
            groups.none { group -> isExcludedGroup(parent, group) }
        } else {
            true
        }
    }

    private fun isExcludedGroup(group: Group, groupId: String): Boolean {
        return groupId == group.id.toString() ||
            groupId.equals(group.name, ignoreCase = true) ||
            groupId.equals(group.fullName, ignoreCase = true)
    }
}
