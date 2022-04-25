package com.roche.ambassador.model.source

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.group.Group
import java.time.LocalDate

object InvalidProjectCriteria {

    fun hasDefaultBranch(): CriterionVerifier = { defaultBranch != null }

    fun isRepositoryNotEmpty(): CriterionVerifier = { !empty }

    fun canCreateMergeRequest(): CriterionVerifier = { permissions.pullRequests.canEveryoneAccess() }

    fun canForkProject(): CriterionVerifier = { permissions.forks.canEveryoneAccess() }

    fun hasVisibilityAtMost(visibility: Visibility): CriterionVerifier = { this.visibility.isMoreStrictThan(visibility) }

    fun excludeArchived(): CriterionVerifier = { !archived }

    fun hasActivityAfter(date: LocalDate): CriterionVerifier = {
        lastActivityDate != null && lastActivityDate.isAfter(date)
    }

    fun excludeForked(): CriterionVerifier = { !forked }

    fun excludeGroups(groups: List<String>): CriterionVerifier = {
        if (parent != null) {
            groups.none { group -> isExcludedGroup(parent, group) }
        } else {
            true
        }
    }

    private fun isExcludedGroup(group: Group, groupId: String): Boolean {
        return groupId == group.id.toString() ||
            groupId.equals(group.fullName, ignoreCase = true)
    }
}
