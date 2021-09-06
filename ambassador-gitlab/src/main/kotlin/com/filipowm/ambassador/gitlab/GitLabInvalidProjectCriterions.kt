package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.model.criteria.IndexingCriterion
import com.filipowm.ambassador.model.source.InvalidProjectCriterions
import com.filipowm.gitlab.api.project.model.Project

object GitLabInvalidProjectCriterions : InvalidProjectCriterions<Project> {
    override fun hasRepositorySetUp(): IndexingCriterion<Project> = RepositorySetUpCriterion

    object RepositorySetUpCriterion: IndexingCriterion<Project> {
        override fun evaluate(input: Project) = input.defaultBranch != null && input.httpUrlToRepo != null
    }
}