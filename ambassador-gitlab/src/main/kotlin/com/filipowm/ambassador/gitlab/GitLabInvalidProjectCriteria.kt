package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.InvalidProjectCriteria
import com.filipowm.gitlab.api.project.model.Project

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
}
