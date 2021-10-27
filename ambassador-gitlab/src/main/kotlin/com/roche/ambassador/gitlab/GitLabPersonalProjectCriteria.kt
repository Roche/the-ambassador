package com.roche.ambassador.gitlab

import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.PersonalProjectCriteria
import com.roche.gitlab.api.project.model.NamespaceKind
import com.roche.gitlab.api.project.model.Project

internal object GitLabPersonalProjectCriteria : PersonalProjectCriteria<Project> {
    override fun hasAtLeastStars(starsCount: Int): CriterionVerifier<Project> = {
        it.ifPersonalProject { it.starCount != null && it.starCount!! > starsCount }
    }

    private fun Project.ifPersonalProject(block: () -> Boolean) = if (this.namespace?.kind == NamespaceKind.USER) {
        block()
    } else {
        true
    }

}
