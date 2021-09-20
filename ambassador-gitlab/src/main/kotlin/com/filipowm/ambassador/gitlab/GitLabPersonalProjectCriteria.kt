package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.PersonalProjectCriteria
import com.filipowm.gitlab.api.project.model.NamespaceKind
import com.filipowm.gitlab.api.project.model.Project

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
